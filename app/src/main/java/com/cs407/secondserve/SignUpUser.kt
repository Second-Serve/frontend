package com.cs407.secondserve

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import android.Manifest
import com.android.volley.VolleyError
import com.cs407.secondserve.model.AccountType
import com.cs407.secondserve.model.User
import com.cs407.secondserve.model.UserRegistrationInfo

class SignUpUser : AppCompatActivity() {

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val CAMERA_PERMISSION_CODE = 101
    }

    private var scannedBarcode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_sign_up_user)

        val firstNameField: EditText = findViewById(R.id.first_name_input)
        val lastNameField: EditText = findViewById(R.id.last_name_input)
        val emailField: EditText = findViewById(R.id.email_input)
        val passwordField: EditText = findViewById(R.id.password_input)
        val confirmPasswordField: EditText = findViewById(R.id.confirm_password_input)
        val termsCheckbox: CheckBox = findViewById(R.id.terms_checkbox)
        val signUpButton: Button = findViewById(R.id.sign_up_button)
        val scanWiscardLink: TextView = findViewById(R.id.scan_wiscard_link)


        scanWiscardLink.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            } else {
                openCamera()
            }
        }


        signUpButton.setOnClickListener {
            val firstName = firstNameField.text.toString().trim()
            val lastName = lastNameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val confirmPassword = confirmPasswordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!termsCheckbox.isChecked) {
                Toast.makeText(this, "You must agree to the terms", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            if (scannedBarcode.isNullOrEmpty() || !isValidBarcode(scannedBarcode!!)) {
//                Toast.makeText(this, "Please scan your wiscard", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }

            // Making Wiscard scanning optional for testing
            if (scannedBarcode != null && !isValidBarcode(scannedBarcode!!)) {
                Toast.makeText(this, "Invalid Wiscard. Please scan again.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val registrationInfo = UserRegistrationInfo(
                accountType = AccountType.CUSTOMER,
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName
            )
            UserAPI.registerAccount(
                registrationInfo,
                onSuccess = { user: User ->
                    Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show()

                    UserAPI.user = user
                    UserAPI.saveUser(applicationContext)

                    val intent = Intent(this, RestaurantSearch::class.java)
                    startActivity(intent)
                    finish()
                },
                onError = { _: VolleyError, message: String ->
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            processBarcode(imageBitmap)
        }
    }

    private fun processBarcode(bitmap: Bitmap) {
        val barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()

        if (!barcodeDetector.isOperational) {
            Toast.makeText(this, "Barcode detector is not operational", Toast.LENGTH_SHORT).show()
            return
        }

        val frame = Frame.Builder().setBitmap(bitmap).build()
        val barcodes = barcodeDetector.detect(frame)

        if (barcodes.size() > 0) {
            val barcode = barcodes.valueAt(0)
            Toast.makeText(this, "Scanned: ${barcode.displayValue}", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "No barcode detected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidBarcode(barcode: String): Boolean {
        return barcode.length == 10 && barcode.startsWith("9") && barcode.all { it.isDigit() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
//        userApiService.cancelAllRequests()
    }
}
