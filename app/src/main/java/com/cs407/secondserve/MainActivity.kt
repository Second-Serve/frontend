package com.cs407.secondserve

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cs407.secondserve.model.AccountType
import com.cs407.secondserve.model.RestaurantRegistrationInfo
import com.cs407.secondserve.model.UserRegistrationInfo
import com.cs407.secondserve.model.WeeklyPickupHours

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_get_started)

        val userSignUpButton: Button = findViewById(R.id.customer_button)
        val restaurantSignUpButton: Button = findViewById(R.id.business_button)

        userSignUpButton.setOnClickListener { loadUserSignUpLayout() }
        restaurantSignUpButton.setOnClickListener { loadRestaurantSignUpLayout() }
    }

    private fun loadUserSignUpLayout() {
        // Load User Sign Up layout
        setContentView(R.layout.fragment_sign_up_user) // Replace with the ID of your user sign-up XML file

        val signUpButton: Button = findViewById(R.id.sign_up_button)
        signUpButton.setOnClickListener {
            val firstNameInput: EditText = findViewById(R.id.first_name_input)
            val lastNameInput: EditText = findViewById(R.id.last_name_input)
            val emailInput: EditText = findViewById(R.id.email_input)
            val passwordInput: EditText = findViewById(R.id.password_input)
            val confirmPasswordInput: EditText = findViewById(R.id.confirm_password_input)

            val firstName = firstNameInput.text.toString()
            val lastName = lastNameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            if (password == confirmPassword) {
                Toast.makeText(this, "User Signed Up!", Toast.LENGTH_SHORT).show()
                val fragment = RestaurantSearch()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.RestaurantSearchScreen, fragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadRestaurantSignUpLayout() {
        // Load Restaurant Sign Up layout
        setContentView(R.layout.fragment_restaurant_sign_up) // Replace with the ID of your restaurant sign-up XML file

        val continueButton: Button = findViewById(R.id.continueButton)
        continueButton.setOnClickListener {
            val firstNameInput: EditText = findViewById(R.id.firstNameInput)
            val lastNameInput: EditText = findViewById(R.id.lastNameInput)
            val restaurantName: EditText = findViewById(R.id.restaurantName)
            val address: EditText = findViewById(R.id.address)
            val startTime: EditText = findViewById(R.id.startTime)
            val endTime: EditText = findViewById(R.id.endTime)

            val firstName = firstNameInput.text.toString()
            val lastName = lastNameInput.text.toString()
            val restaurant = restaurantName.text.toString()
            val addressText = address.text.toString()
            val start = startTime.text.toString()
            val end = endTime.text.toString()

            if (restaurant.isNotEmpty() && addressText.isNotEmpty() && start.isNotEmpty() && end.isNotEmpty()) {
                Toast.makeText(this, "Restaurant Signed Up!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }


override fun onStop() {
        super.onStop()
    }
}