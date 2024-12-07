package com.cs407.secondserve

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import com.cs407.secondserve.model.AccountType
import com.cs407.secondserve.model.User
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.camera.view.PreviewView
import androidx.camera.core.Preview
import com.cs407.secondserve.service.AccountService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.Location
import androidx.annotation.OptIn
import com.cs407.secondserve.service.LocationService
import com.cs407.secondserve.util.Debug
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.textfield.TextInputLayout

class UserSignUpView : SecondServeView() {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 101
        private const val LOCATION_PERMISSION_CODE = 102
    }

    private var scannedBarcode: String? = null

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocation: Location? = null

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                processBarcode(bitmap)
            } else {
                Toast.makeText(this, "No image data", Toast.LENGTH_SHORT).show()
            }
        }

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_sign_up_user)

        firebaseAuth = FirebaseAuth.getInstance()

        cameraExecutor = Executors.newSingleThreadExecutor()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LocationService.requestLocation(this)
        } else {
            getUserLocation()
        }

        val firstNameField: EditText = findViewById<TextInputLayout>(R.id.firstNameInputLayout).editText!!
        val lastNameField: EditText = findViewById<TextInputLayout>(R.id.lastNameInputLayout).editText!!
        val emailField: EditText = findViewById<TextInputLayout>(R.id.email_input).editText!!
        val passwordField: EditText = findViewById<TextInputLayout>(R.id.passwordInputLayout).editText!!
        val confirmPasswordField: EditText = findViewById<TextInputLayout>(R.id.confirmPasswordInputLayout).editText!!
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
                val previewView = findViewById<PreviewView>(R.id.viewFinder)
                previewView.visibility = View.VISIBLE
                startCamera()
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

            val isValidBarcode = !scannedBarcode.isNullOrEmpty() && isValidBarcode(scannedBarcode!!)
            if (!Debug.SKIP_WISCARD_SCANNING && !isValidBarcode) {
                Toast.makeText(this, "Please scan your wiscard", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Now, proceed with registering the user in the AccountService
            AccountService.register(
                email,
                password,
                AccountType.CUSTOMER,
                firstName,
                lastName,
                onSuccess = { user: User ->
                    firebaseAuth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Sign up successful! Check your email for verification.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                startActivityEmptyIntent(LoginView::class.java)
                                finish()

                            } else {
                                Toast.makeText(
                                    this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            )
        }
    }

    private fun checkIfEmailVerified() {
        val user = firebaseAuth.currentUser
        user?.reload()?.addOnCompleteListener { reloadTask ->
            if (reloadTask.isSuccessful) {
                if (user.isEmailVerified) {
                    Toast.makeText(this, "Email is verified!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please verify your email.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val previewView = findViewById<PreviewView>(R.id.viewFinder)
            previewView.visibility = View.VISIBLE

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder().build()
            val barcodeScanner = BarcodeScanning.getClient()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image = InputImage.fromMediaImage(
                        mediaImage,
                        imageProxy.imageInfo.rotationDegrees
                    )

                    barcodeScanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            if (barcodes.isNotEmpty()) {
                                val barcode = barcodes.first()
                                scannedBarcode = barcode.displayValue

                                previewView.visibility = View.GONE

                                if (scannedBarcode != null && isValidBarcode(scannedBarcode!!)) {
                                    val snackbar = com.google.android.material.snackbar.Snackbar.make(
                                        findViewById(android.R.id.content),
                                        "Valid Barcode: $scannedBarcode",
                                        10000
                                    )
                                    snackbar.show()
                                } else {
                                    Toast.makeText(this, "Invalid Barcode: $scannedBarcode", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Barcode detection failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Toast.makeText(this, "Camera initialization failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun getUserLocation() {

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        userLocation = location
                        Toast.makeText(this, "Your location: ${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Unable to fetch location. Try again.", Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Error fetching location: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

    }

    private fun isValidBarcode(barcode: String): Boolean {
        return barcode.length == 11 && barcode.startsWith("9") && barcode.all { it.isDigit() }
    }


private fun processBarcode(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val scanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    val barcode = barcodes.first()
                    scannedBarcode = barcode.displayValue
                    Toast.makeText(this, "Scanned: $scannedBarcode", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "No barcode detected", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Barcode detection failed: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, fetch location
                getUserLocation()
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied. Unable to fetch location.", Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onStop() {
        super.onStop()
        cameraExecutor.shutdown()
    }
}
