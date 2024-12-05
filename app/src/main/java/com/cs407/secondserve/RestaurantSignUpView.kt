package com.cs407.secondserve

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.tools.build.jetifier.core.utils.Log
import com.cs407.secondserve.model.AccountType
import com.cs407.secondserve.service.AccountService
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject

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

            AccountService.register(
                email,
                password,
                AccountType.BUSINESS,
                restaurantName = restaurantName,
                pickupStartTime = "09:00", // TODO: Make these configurable
                pickupEndTime = "21:00",
                address = address,
                onSuccess = {
                    Log.d("SignUp", "onSuccess called")
                    Toast.makeText(this@RestaurantSignUpView, "Sign up successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RestaurantSignUpView, RestaurantMainView::class.java)
                    startActivity(intent)
                    finish()
                },
                onFailure = { exception ->
                    Log.e("SignUp", "onFailure called: ${exception.message}")
                    Toast.makeText(baseContext, exception.message, Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    private suspend fun geocodeAddressUsingGoogleMaps(address: String): LatLng? {
        return try {
            val result = Firebase.functions
                .getHttpsCallable("geocodeAddress")
                .call(hashMapOf(
                    "address" to address
                ))
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                }
                .await()

            val data = JSONObject(result.getData() as MutableMap<Any?, Any?>)
            val location = data
                .getJSONArray("results")
                .getJSONObject(0)
                .getJSONObject("geometry")
                .getJSONObject("location")
            val latitude = location.getDouble("lat")
            val longitude = location.getDouble("lng")

            LatLng(latitude, longitude)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(baseContext, "Error during geocoding: ${e.message}", Toast.LENGTH_LONG).show()
            }
            e.printStackTrace()
            null
        }
    }

    private suspend fun isAddressValid(address: String): Boolean {
        try {
            val result = Firebase.functions
                .getHttpsCallable("isAddressValid")
                .call(
                    hashMapOf(
                        "address" to address
                    )
                )
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                }
                .await()

            val data = JSONObject(result.getData() as MutableMap<Any?, Any?>)
            val isValid = data.getBoolean("isValid")
            return isValid
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(baseContext, "Error during address validation: ${e.message}", Toast.LENGTH_LONG).show()
            }
            e.printStackTrace()
            return false
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show()
    }
}