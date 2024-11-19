package com.cs407.secondserve

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        UserAPI.init(this)

        val userLogInButton: Button = findViewById(R.id.button)
        val userSignUpButton: Button = findViewById(R.id.button2)

        userSignUpButton.setOnClickListener { loadSignUp() }
        userLogInButton.setOnClickListener { loadLogIn() }
    }

    private fun loadSignUp() {
        val intent = Intent(this, GetStarted::class.java)
        startActivity(intent)

//        val userSignUpButton: Button = findViewById(R.id.customer_button)
//        val restaurantSignUpButton: Button = findViewById(R.id.business_button)
//        userSignUpButton.setOnClickListener { loadUserSignUpLayout() }
//        restaurantSignUpButton.setOnClickListener { loadRestaurantSignUpLayout() }
    }

    private fun loadLogIn() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun loadUserSignUpLayout() {
        val intent = Intent(this, SignUpUser::class.java)
        startActivity(intent)
//        finish()

        // TODO: this should probably be in SignUpUser
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

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()
            ) {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "User Signed Up!", Toast.LENGTH_SHORT).show()
                val restaurantSearchIntent = Intent(this, RestaurantSearch::class.java)
                startActivity(restaurantSearchIntent)
            }

        }
    }

    private fun loadRestaurantSignUpLayout() {
        setContentView(R.layout.fragment_restaurant_sign_up)

        // Access the continue button after setting the content view
        val continueButton: Button = findViewById(R.id.restaurant_sign_up_button)
        continueButton.setOnClickListener {
            val firstNameInput: EditText = findViewById(R.id.restaurant_first_name_input)
            val lastNameInput: EditText = findViewById(R.id.restaurant_last_name_input)
            val restaurantName: EditText = findViewById(R.id.restaurant_name_input)
            val address: EditText = findViewById(R.id.restaurant_address_input)
            val startTime: EditText = findViewById(R.id.restaurant_pickup_hours_start_time_input)
            val endTime: EditText = findViewById(R.id.restaurant_pickup_hours_end_time_input)

            val firstName = firstNameInput.text.toString().trim()
            val lastName = lastNameInput.text.toString().trim()
            val restaurant = restaurantName.text.toString().trim()
            val addressText = address.text.toString().trim()
            val start = startTime.text.toString().trim()
            val end = endTime.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty() || restaurant.isEmpty() ||
                addressText.isEmpty() || start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Restaurant Signed Up!", Toast.LENGTH_SHORT).show()
                val restaurantSearchIntent = Intent(this, RestaurantSearch::class.java)
                startActivity(restaurantSearchIntent)
                finish()

            }
        }

        fun restaurantSearch(){
            val intent = Intent(this, RestaurantSearch::class.java)
            startActivity(intent)

        }
    }


    override fun onStop() {
        super.onStop()
    }
}