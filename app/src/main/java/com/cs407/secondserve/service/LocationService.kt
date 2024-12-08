package com.cs407.secondserve.service

import android.Manifest
import android.R.attr.bitmap
import android.R.attr.data
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cs407.secondserve.model.MapImageType
import com.google.android.gms.location.LocationServices
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationService {
    companion object {
        const val TAG = "LocationService"

        private const val MILES_PER_METER = 1 / 1609.34
        private const val LOCATION_PERMISSION_CODE = 102

        var userLocation: Location? = null

        /**
         * Validates the given address using Firebase.
         */
        fun validateAddress(
            address: String,
            onSuccess: ((Boolean, String?) -> Unit)? = null,
            onFailure: ((Exception) -> Unit)? = null
        ) {
            if (address.isBlank()) {
                Log.d(TAG, "Address is empty or null")
                onSuccess?.invoke(false, null)
                return
            }

            try {
                val data = hashMapOf("address" to address)
                Firebase.functions
                    .getHttpsCallable("isAddressValid")
                    .call(data)
                    .addOnSuccessListener { result ->
                        val resultMap = result.getData() as? Map<*, *>
                        val isValidAddress = resultMap?.get("isValid") as? Boolean ?: false
                        val reason: String? = if (!isValidAddress) {
                            resultMap?.get("reason") as? String
                        } else null
                        onSuccess?.invoke(isValidAddress, reason)
                    }
                    .addOnFailureListener { exception ->
                        onFailure?.invoke(exception)
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error validating address: ${e.message}", e)
                onFailure?.invoke(e)
            }
        }

        /**
         * Fetches the distance to the restaurant from a given starting location.
         */
        fun getDistanceToRestaurant(
            startingPoint: Location,
            restaurantId: String,
            onSuccess: ((Double) -> Unit)? = null,
            onFailure: ((Exception) -> Unit)? = null
        ) {
            val args = hashMapOf(
                "startingPoint" to hashMapOf(
                    "latitude" to startingPoint.latitude,
                    "longitude" to startingPoint.longitude
                ),
                "restaurantId" to restaurantId
            )

            Firebase.functions.getHttpsCallable("distanceToRestaurant")
                .call(args)
                .addOnSuccessListener { result ->
                    val data = JSONObject(result.getData() as Map<*, *>)
                    val distanceMiles = data.getDouble("distance") * MILES_PER_METER
                    onSuccess?.invoke(distanceMiles)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error fetching distance: ${exception.message}", exception)
                    onFailure?.invoke(exception)
                }
        }

        fun getDistanceToRestaurant(
            restaurantId: String,
            onSuccess: ((Double) -> Unit)? = null,
            onFailure: ((Exception) -> Unit)? = null
        ) {
            if (userLocation == null) {
                Log.e(TAG, "User location is null")
                onFailure?.invoke(Exception("User location is null"))
                return
            }

            getDistanceToRestaurant(userLocation!!, restaurantId, onSuccess, onFailure)
        }

        /**
         * Fetches the map image for a restaurant and returns it as a Bitmap.
         */
        fun getRestaurantMapImage(
            restaurantId: String,
            type: MapImageType,
            onSuccess: (Bitmap) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val args = hashMapOf(
                "restaurantId" to restaurantId,
                "type" to type.toString()
            )

            Firebase.functions.getHttpsCallable("getRestaurantMapImage")
                .call(args)
                .addOnSuccessListener { result ->
                    try {
                        // Assuming backend returns an image URL
                        val imageUrl = result.getData() as String
                        fetchImageFromUrl(imageUrl, onSuccess, onFailure)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing map image response: ${e.message}")
                        onFailure(e)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Failed to fetch restaurant map image: ${exception.message}")
                    onFailure(exception)
                }
        }

        /**
         * Fetches an image from a URL and decodes it into a Bitmap.
         */
        private fun fetchImageFromUrl(
            imageUrl: String,
            onSuccess: (Bitmap) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val connection = java.net.URL(imageUrl).openConnection()
                    connection.connect()
                    val inputStream = connection.getInputStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    withContext(Dispatchers.Main) {
                        onSuccess(bitmap)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching image from URL: ${e.message}")
                    withContext(Dispatchers.Main) {
                        onFailure(e)
                    }
                }
            }
        }

        /**
         * Updates the user's location.
         */
        fun updateUserLocation(location: Location?) {
            userLocation = location
        }

        /**
         * Checks whether the app has location permissions.
         */
        fun hasLocationPermissions(context: Context): Boolean {
            return ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }

        /**
         * Gets the device's last known location.
         */
        fun getDeviceLocation(
            context: Context,
            onComplete: ((Location?) -> Unit)? = null
        ) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            if (!hasLocationPermissions(context)) {
                onComplete?.invoke(null)
                return
            }

            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        onComplete?.invoke(location)
                    }
            } catch (exception: SecurityException) {
                Log.e(TAG, "Error fetching device location: ${exception.message}", exception)
                onComplete?.invoke(null)
            }
        }

        /**
         * Requests location permissions.
         */
        fun requestLocation(
            activity: Activity,
            onComplete: ((Location?) -> Unit)? = null
        ) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

            val fineLocationPermission = ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            if (fineLocationPermission == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        userLocation = location
                        onComplete?.invoke(location)
                    } else {
                        Toast.makeText(activity, "Unable to fetch location. Try again.", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_CODE
                )
            }
        }
    }
}