package com.cs407.secondserve

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import android.Manifest
import android.view.View
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import com.cs407.secondserve.model.AccountType
import com.cs407.secondserve.service.AccountService
import com.cs407.secondserve.model.UserRegistrationInfo
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UserSignUpView : SecondServeView() {

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val CAMERA_PERMISSION_CODE = 101
        private const val LOCATION_PERMISSION_CODE = 102
    }

    private var scannedBarcode: String? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocation: Location? = null

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
        val scanWiscardButton: TextView = findViewById(R.id.scan_wiscard_button)

        cameraExecutor = Executors.newSingleThreadExecutor()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
                openCamera()
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getUserLocation()
        } else {
            requestLocationPermission()
        }

        val signUpButton: Button = findViewById(R.id.sign_up_button)
        signUpButton.setOnClickListener {
            handleUserSignUp()
        }
    }

    private fun handleUserSignUp() {
        val email = findViewById<EditText>(R.id.email_input).text.toString().trim()
        val password = findViewById<EditText>(R.id.password_input).text.toString().trim()
        val firstName = findViewById<EditText>(R.id.first_name_input).text.toString().trim()
        val lastName = findViewById<EditText>(R.id.last_name_input).text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (userLocation == null) {
            Toast.makeText(this, "Unable to fetch location. Please try again.", Toast.LENGTH_SHORT).show()
            return
        }

        val registrationInfo = UserRegistrationInfo(
            accountType = AccountType.CUSTOMER,
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName,
            latitude = userLocation?.latitude,
            longitude = userLocation?.longitude
        )

        UserAPI.registerAccount(
            registrationInfo,
            onSuccess = {
                Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, RestaurantSearch::class.java))
                finish()
            },
            onError = { _, message ->
                Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun processBarcode(bitmap: Bitmap) {
        val barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()

        if (!barcodeDetector.isOperational) {
            Toast.makeText(baseContext, "Barcode detector is not operational", Toast.LENGTH_SHORT).show()
            return
        }

        val frame = Frame.Builder().setBitmap(bitmap).build()
        val barcodes = barcodeDetector.detect(frame)

        if (barcodes.size() > 0) {
            val barcode = barcodes.valueAt(0)
            Toast.makeText(baseContext, "Scanned: ${barcode.displayValue}", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(baseContext, "No barcode detected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            userLocation = location ?: run {
                Toast.makeText(this, "Unable to fetch location.", Toast.LENGTH_SHORT).show()
                null
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get location: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_CODE
        )
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

        when (requestCode) {
            LOCATION_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation()
                } else {
                    Toast.makeText(this, "Location permission is required for registration.", Toast.LENGTH_SHORT).show()
                }
            }
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera()
                } else {
                    Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @OptIn(ExperimentalGetImage::class) private fun startCamera() {
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
                                scannedBarcode = barcodes.first().displayValue
                                Toast.makeText(this, "Scanned: $scannedBarcode", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Barcode scanning failed: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            } catch (e: Exception) {
                Toast.makeText(this, "Camera initialization failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}