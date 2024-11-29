package com.cs407.secondserve

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SignUpUser : AppCompatActivity() {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 101
    }

    private lateinit var cameraExecutor: ExecutorService
    private var cameraProvider: ProcessCameraProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_sign_up_user)

        cameraExecutor = Executors.newSingleThreadExecutor()

        val scanWiscardButton: TextView = findViewById(R.id.scan_wiscard_button)
        val resultTextView: TextView = findViewById(R.id.result_text)
        val previewView: PreviewView = findViewById(R.id.viewFinder)

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
                previewView.visibility = View.VISIBLE
                startCamera { wiscardNumber ->
                    if (wiscardNumber != null) {
                        resultTextView.text = "Scanned: $wiscardNumber\n"
                        if (isValidWiscard(wiscardNumber)) {
                            resultTextView.append("Wiscard Valid!")
                        } else {
                            resultTextView.append("Wiscard Invalid!")
                        }
                    } else {
                        resultTextView.text = "No valid barcode detected."
                    }
                    stopCamera()
                }
            }
        }
    }

    private fun isValidWiscard(wiscardNumber: String): Boolean {
        // Replace with your own validation logic for the Wiscard
        return wiscardNumber.length == 10 && wiscardNumber.startsWith("9") && wiscardNumber.all { it.isDigit() }
    }

    private fun stopCamera() {
        cameraProvider?.unbindAll()
    }

    @androidx.camera.core.ExperimentalGetImage
    private fun startCamera(onScanned: (String?) -> Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            val previewView: PreviewView = findViewById(R.id.viewFinder)
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            val barcodeScanner = BarcodeScanning.getClient()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    barcodeScanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            if (barcodes.isNotEmpty()) {
                                val barcode = barcodes.first().displayValue
                                onScanned(barcode)
                            }
                        }
                        .addOnFailureListener {
                            onScanned(null)
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            } catch (e: Exception) {
                Toast.makeText(this, "Camera initialization failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

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
                startCamera { wiscardNumber ->
                }
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
