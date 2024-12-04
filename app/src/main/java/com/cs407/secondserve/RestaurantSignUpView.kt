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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

class RestaurantSignUpView : SecondServeView() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_restaurant_sign_up)

        val emailField: EditText = findViewById(R.id.restaurant_email_input)
        val passwordField: EditText = findViewById(R.id.restaurant_password_input)
        val restaurantNameField: EditText = findViewById(R.id.restaurant_name_input)
        val restaurantAddressField: EditText = findViewById(R.id.restaurant_address_input)
        val signUpButton: Button = findViewById(R.id.restaurant_sign_up_button)

        signUpButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val restaurantName = restaurantNameField.text.toString().trim()
            val address = restaurantAddressField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || restaurantName.isEmpty() || address.isEmpty()) {
                Toast.makeText(baseContext, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showToast("Starting geocoding for address: $address")

            CoroutineScope(Dispatchers.IO).launch {
                val location = geocodeAddressUsingGoogleMaps(address)
                withContext(Dispatchers.Main) {
                    if (location == null) {
                        showToast("Geocoding failed: Location is null")
                    } else {
                        showToast("Geocoded location: ${location.latitude}, ${location.longitude}")
                    }
                }

                if (location == null || !isInMadison(location)) {
                    withContext(Dispatchers.Main) {
                        showToast("Address is not within the Madison area.")
                    }
                } else {
                    showToast("Location is within Madison: ${location.latitude}, ${location.longitude}")
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

    private suspend fun geocodeAddressUsingGoogleMaps(address: String): LatLng? {
        val apiKey = "AIzaSyDSN2PBeyfJCDx7ahH7z23-A-Nuy7h9nNs"
        val encodedAddress = URLEncoder.encode(address, "UTF-8")
        val url = "https://maps.googleapis.com/maps/api/geocode/json?address=$encodedAddress&key=$apiKey"

        return try {
            withContext(Dispatchers.Main) {
                showToast("Geocode API request: $url")
            }

            val response = withContext(Dispatchers.IO) { URL(url).readText() }

            withContext(Dispatchers.Main) {
                showToast("Geocode API response: $response")
            }

            val jsonObject = JSONObject(response)

            // Check for API errors in the response
            val status = jsonObject.getString("status")
            if (status != "OK") {
                withContext(Dispatchers.Main) {
                    showToast("Geocode API error: $status")
                }
                return null
            }

            val location = jsonObject.getJSONArray("results")
                .getJSONObject(0)
                .getJSONObject("geometry")
                .getJSONObject("location")
            val lat = location.getDouble("lat")
            val lng = location.getDouble("lng")
            LatLng(lat, lng)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                showToast("Error during geocoding: ${e.message}")
            }
            e.printStackTrace()
            null
        }
    }

    private suspend fun isInMadison(location: LatLng): Boolean {
        val apiKey = "AIzaSyDSN2PBeyfJCDx7ahH7z23-A-Nuy7h9nNs"
        val latLngString = "${location.latitude},${location.longitude}"
        val url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=$latLngString&key=$apiKey"

        return try {
            withContext(Dispatchers.Main) {
                showToast("Reverse Geocode API request URL: $url")
            }
            val response = withContext(Dispatchers.IO) { URL(url).readText() }
            withContext(Dispatchers.Main) {
                showToast("Reverse Geocode API response received")
            }
            val jsonObject = JSONObject(response)
            val results = jsonObject.getJSONArray("results")

            for (i in 0 until results.length()) {
                val formattedAddress = results.getJSONObject(i).getString("formatted_address").lowercase()
                withContext(Dispatchers.Main) {
                    showToast("Checking address: $formattedAddress")
                }
                if (formattedAddress.contains("madison")) {
                    withContext(Dispatchers.Main) {
                        showToast("Address is in Madison")
                    }
                    return true
                }
            }
            withContext(Dispatchers.Main) {
                showToast("Address is not in Madison")
            }
            false
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                showToast("Error during reverse geocoding: ${e.message}")
            }
            e.printStackTrace()
            false
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show()
    }
}