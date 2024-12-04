package com.cs407.secondserve

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.cs407.secondserve.model.AccountType
import androidx.fragment.app.Fragment
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

        val emailField: EditText = findViewById(R.id.restaurant_email_input)
        val passwordField: EditText = findViewById(R.id.restaurant_password_input)
        val restaurantNameField: EditText = findViewById(R.id.restaurant_name_input)
        val restaurantAddressField: EditText = findViewById(R.id.restaurant_address_input)
        val signUpButton: Button = findViewById(R.id.restaurant_sign_up_button)

        val checkboxMonday: CheckBox = findViewById(R.id.checkbox_monday)
        val checkboxTuesday: CheckBox = findViewById(R.id.checkbox_tuesday)
        val checkboxWednesday: CheckBox = findViewById(R.id.checkbox_wednesday)
        val checkboxThursday: CheckBox = findViewById(R.id.checkbox_thursday)
        val checkboxFriday: CheckBox = findViewById(R.id.checkbox_friday)
        val checkboxSaturday: CheckBox = findViewById(R.id.checkbox_saturday)
        val checkboxSunday: CheckBox = findViewById(R.id.checkbox_sunday)

        val startTimeMonday: Spinner = findViewById(R.id.pickup_start_time_monday)
        val endTimeMonday: Spinner = findViewById(R.id.pickup_end_time_monday)
        val startTimeTuesday: Spinner = findViewById(R.id.pickup_start_time_tuesday)
        val endTimeTuesday: Spinner = findViewById(R.id.pickup_end_time_tuesday)
        val startTimeWednesday: Spinner = findViewById(R.id.pickup_start_time_wednesday)
        val endTimeWednesday: Spinner = findViewById(R.id.pickup_end_time_wednesday)
        val startTimeThursday: Spinner = findViewById(R.id.pickup_start_time_thursday)
        val endTimeThursday: Spinner = findViewById(R.id.pickup_end_time_thursday)
        val startTimeFriday: Spinner = findViewById(R.id.pickup_start_time_friday)
        val endTimeFriday: Spinner = findViewById(R.id.pickup_end_time_friday)
        val startTimeSaturday: Spinner = findViewById(R.id.pickup_start_time_saturday)
        val endTimeSaturday: Spinner = findViewById(R.id.pickup_end_time_saturday)
        val startTimeSunday: Spinner = findViewById(R.id.pickup_start_time_sunday)
        val endTimeSunday: Spinner = findViewById(R.id.pickup_end_time_sunday)


        signUpButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val restaurantName = restaurantNameField.text.toString().trim()
            val address = restaurantAddressField.text.toString().trim()

            val pickupHours = mutableMapOf<String, Pair<String, String>>()

            if (checkboxMonday.isChecked) {
                pickupHours["Monday"] = Pair(
                    startTimeMonday.selectedItem.toString(),
                    endTimeMonday.selectedItem.toString()
                )
            }
            if (checkboxTuesday.isChecked) {
                pickupHours["Tuesday"] = Pair(
                    startTimeTuesday.selectedItem.toString(),
                    endTimeTuesday.selectedItem.toString()
                )
            }
            if (checkboxWednesday.isChecked) {
                pickupHours["Wednesday"] = Pair(
                    startTimeWednesday.selectedItem.toString(),
                    endTimeWednesday.selectedItem.toString()
                )
            }
            if (checkboxThursday.isChecked) {
                pickupHours["Thursday"] = Pair(
                    startTimeThursday.selectedItem.toString(),
                    endTimeThursday.selectedItem.toString()
                )
            }
            if (checkboxFriday.isChecked) {
                pickupHours["Friday"] = Pair(
                    startTimeFriday.selectedItem.toString(),
                    endTimeFriday.selectedItem.toString()
                )
            }
            if (checkboxSaturday.isChecked) {
                pickupHours["Saturday"] = Pair(
                    startTimeSaturday.selectedItem.toString(),
                    endTimeSaturday.selectedItem.toString()
                )
            }
            if (checkboxSunday.isChecked) {
                pickupHours["Sunday"] = Pair(
                    startTimeSunday.selectedItem.toString(),
                    endTimeSunday.selectedItem.toString()
                )
            }

            if (email.isEmpty() || password.isEmpty() || restaurantName.isEmpty() ||
                address.isEmpty() || pickupHours.isEmpty()) {
                Toast.makeText(baseContext, "Please fill in all fields and select at least one pickup day", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val pickupTimesString = pickupHours.entries.joinToString("; ") {
                "${it.key}: ${it.value.first} - ${it.value.second}"
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
                            AccountType.BUSINESS,
                            restaurantName,
                            address,
                            onSuccess = {
                                Toast.makeText(this@RestaurantSignUpView, "Sign up successful!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@RestaurantSignUpView, RestaurantMainView::class.java)
                                startActivity(intent)
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