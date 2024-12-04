package com.cs407.secondserve

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.cs407.secondserve.model.AccountType
import com.cs407.secondserve.service.AccountService
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RestaurantSignUpView : SecondServeView() {

    private val madisonBoundary = listOf(
        LatLng(43.017086, -89.552289),
        LatLng(43.165309, -89.276369),
        LatLng(43.083675, -89.561704),
        LatLng(43.095319, -89.483126),
        LatLng(43.076189, -89.462627),
        LatLng(43.162639, -89.365260),
        LatLng(43.156824, -89.403410),
        LatLng(43.146855, -89.332234),
        LatLng(43.172606, -89.294654),
        LatLng(43.106544, -89.247393),
        LatLng(43.027508, -89.247963)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_restaurant_sign_up)

        val firstNameField: EditText = findViewById(R.id.restaurant_first_name_input)
        val lastNameField: EditText = findViewById(R.id.restaurant_last_name_input)
        val emailField: EditText = findViewById(R.id.restaurant_email_input)
        val passwordField: EditText = findViewById(R.id.restaurant_password_input)
        val restaurantNameField: EditText = findViewById(R.id.restaurant_name_input)
        val restaurantAddressField: EditText = findViewById(R.id.restaurant_address_input)
        val restaurantPickupHoursStartField: EditText = findViewById(R.id.restaurant_pickup_hours_start_time_input)
        val restaurantPickupHoursEndField: EditText = findViewById(R.id.restaurant_pickup_hours_end_time_input)
        val signUpButton: Button = findViewById(R.id.restaurant_sign_up_button)

        signUpButton.setOnClickListener {
            val firstName = firstNameField.text.toString().trim()
            val lastName = lastNameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val restaurantName = restaurantNameField.text.toString().trim()
            val address = restaurantAddressField.text.toString().trim()
            val pickupStartTime = restaurantPickupHoursStartField.text.toString().trim()
            val pickupEndTime = restaurantPickupHoursEndField.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() ||
                restaurantName.isEmpty() || address.isEmpty() || pickupStartTime.isEmpty() || pickupEndTime.isEmpty()) {
                Toast.makeText(baseContext, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val location = geocodeAddressWithFirebase(address)
                println("Location from geocodeAddressWithFirebase: $location")
                if (location == null || !isWithinPolygon(location, madisonBoundary)) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(baseContext, "Address is not within the Madison area.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        AccountService.register(
                            email,
                            password,
                            firstName,
                            lastName,
                            AccountType.BUSINESS,
                            restaurantName,
                            address,
                            pickupStartTime,
                            pickupEndTime,
                            onSuccess = {
                                Toast.makeText(baseContext, "Sign up successful!", Toast.LENGTH_SHORT).show()
                                startActivityEmptyIntent(RestaurantSearchView::class.java)
                                finish()
                            },
                            onFailure = { exception ->
                                Toast.makeText(baseContext, exception.message, Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                }
            }
        }
    }

    private suspend fun geocodeAddressWithFirebase(address: String): LatLng? {
        val normalizedAddress = address.trim().lowercase()
        val firestore = FirebaseFirestore.getInstance()
        return try {
            val geoDoc = firestore.collection("geocodes").document(normalizedAddress).get().await()
            if (geoDoc.exists()) {
                val lat = geoDoc.getDouble("lat") ?: return null
                val lng = geoDoc.getDouble("lng") ?: return null
                LatLng(lat, lng)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun isWithinPolygon(location: LatLng, polygon: List<LatLng>): Boolean {
        var intersects = false
        val x = location.longitude
        val y = location.latitude
        for (i in polygon.indices) {
            val xi = polygon[i].longitude
            val yi = polygon[i].latitude
            val xj = polygon[(i + 1) % polygon.size].longitude
            val yj = polygon[(i + 1) % polygon.size].latitude
            val intersect = ((yi > y) != (yj > y)) &&
                    (x < (xj - xi) * (y - yi) / (yj - yi) + xi)
            if (intersect) intersects = !intersects
        }
        return intersects
    }
}