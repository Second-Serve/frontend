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
import android.location.Location
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import com.cs407.secondserve.model.AccountType
import com.cs407.secondserve.model.User
import com.cs407.secondserve.model.UserRegistrationInfo
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.camera.view.PreviewView
import androidx.camera.core.Preview
import androidx.databinding.tool.store.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class SignUpUser : AppCompatActivity() {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 101
    }

    private var scannedBarcode: String? = null

    private lateinit var cameraExecutor: ExecutorService

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                processBarcode(bitmap)
            } else {
                Toast.makeText(this, "No image data", Toast.LENGTH_SHORT).show()
            }
        }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocation: Location? = null

    @androidx.camera.core.ExperimentalGetImage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_sign_up_user)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Request location permission if not already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getUserLocation()
        } else {
            requestLocationPermission()
        }

        // Existing UI elements and click listeners
        val signUpButton: Button = findViewById(R.id.sign_up_button)
        signUpButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.email_input).text.toString().trim()
            val password = findViewById<EditText>(R.id.password_input).text.toString().trim()
            val firstName = findViewById<EditText>(R.id.first_name_input).text.toString().trim()
            val lastName = findViewById<EditText>(R.id.last_name_input).text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userLocation == null) {
                Toast.makeText(this, "Unable to fetch location. Please try again.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val registrationInfo = UserRegistrationInfo(
                accountType = AccountType.CUSTOMER,
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName
            ).apply {
                userLocation?.let {
                    latitude = it.latitude
                    longitude = it.longitude
                }
            }

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
    }

    private fun getUserLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                userLocation = location
            } else {
                Toast.makeText(this, "Unable to fetch location.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get location: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getUserLocation()
        } else {
            Toast.makeText(this, "Location permission is required for registration.", Toast.LENGTH_SHORT).show()
        }
    }

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



    @ExperimentalGetImage
    override fun onStart() {
        super.onStart()
//        startCamera()
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

    private fun isValidBarcode(barcode: String): Boolean {
        return barcode.length == 11 && barcode.startsWith("9") && barcode.all { it.isDigit() }
    }

    @androidx.camera.core.ExperimentalGetImage
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                val previewView = findViewById<PreviewView>(R.id.viewFinder)
                previewView.visibility = View.VISIBLE
                startCamera()
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}