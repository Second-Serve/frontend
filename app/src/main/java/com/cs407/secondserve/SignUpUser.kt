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
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import com.cs407.secondserve.model.AccountType
import com.cs407.secondserve.model.User
import com.cs407.secondserve.model.UserRegistrationInfo
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage

//import androidx.activity.result.contracts.ActivityResultContracts

class SignUpUser : AppCompatActivity() {

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val CAMERA_PERMISSION_CODE = 101
    }

    private var scannedBarcode: String? = null

    // Register the activity result launcher for camera intent
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                processBarcode(bitmap)
            } else {
                Toast.makeText(this, "No image data", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_sign_up_user)

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }

        val firstNameField: EditText = findViewById(R.id.first_name_input)
        val lastNameField: EditText = findViewById(R.id.last_name_input)
        val emailField: EditText = findViewById(R.id.email_input)
        val passwordField: EditText = findViewById(R.id.password_input)
        val confirmPasswordField: EditText = findViewById(R.id.confirm_password_input)
        val termsCheckbox: CheckBox = findViewById(R.id.terms_checkbox)
        val signUpButton: Button = findViewById(R.id.sign_up_button)
        val scanWiscardButton: TextView = findViewById(R.id.scan_wiscard_button)

        scanWiscardButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            } else {
                takePictureLauncher.launch(null)
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

            if (scannedBarcode.isNullOrEmpty() || !isValidBarcode(scannedBarcode!!)) {
                Toast.makeText(this, "Please scan your wiscard", Toast.LENGTH_SHORT).show()
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
                }
            )
        }
    }

    private fun processBarcode(bitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val barcodeDetector = FirebaseVision.getInstance().getVisionBarcodeDetector()
        barcodeDetector.detectInImage(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    val barcode = barcodes.first()
                    scannedBarcode = barcode.displayValue
                    Toast.makeText(this, "Scanned: ${scannedBarcode}", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "No barcode detected", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Barcode detection failed: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()  // Log error for debugging
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
                takePictureLauncher.launch(null)
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

