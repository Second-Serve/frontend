package com.cs407.secondserve

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.cs407.secondserve.model.AccountType
import com.cs407.secondserve.model.DailyPickupHours
import com.cs407.secondserve.model.RestaurantRegistrationInfo
import com.cs407.secondserve.model.UserRegistrationInfo
import com.cs407.secondserve.model.WeeklyPickupHours

class RestaurantSignUpView : SecondServeView() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_restaurant_sign_up)

        val firstNameField: EditText = findViewById(R.id.restaurant_first_name_input)
        val lastNameField: EditText = findViewById(R.id.restaurant_last_name_input)
        val emailField: EditText = findViewById(R.id.restaurant_email_input)
        val passwordField: EditText = findViewById(R.id.restaurant_password_input)
        // val confirmPasswordField: EditText = findViewById(R.id.confirm_password_input) // TODO: Add confirm password field
        val restaurantNameField: EditText = findViewById(R.id.restaurant_name_input)
        val restaurantAddressField: EditText = findViewById(R.id.restaurant_address_input)
        val restaurantPickupHoursStartField: EditText = findViewById(R.id.restaurant_pickup_hours_start_time_input)
        val restaurantPickupHoursEndField: EditText = findViewById(R.id.restaurant_pickup_hours_end_time_input)
        // val termsCheckbox: CheckBox = findViewById(R.id.terms_checkbox) // TODO: Add terms checkbox
        val signUpButton: Button = findViewById(R.id.restaurant_sign_up_button)

        signUpButton.setOnClickListener {
            val firstName = firstNameField.text.toString().trim()
            val lastName = lastNameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val restaurantName = restaurantNameField.text.toString().trim()
            val address = restaurantAddressField.text.toString().trim()
            val pickupStart = restaurantPickupHoursStartField.text.toString().trim()
            val pickupEnd = restaurantPickupHoursEndField.text.toString().trim()

            if (
                firstName.isEmpty()
                || lastName.isEmpty()
                || email.isEmpty()
                || password.isEmpty()
                || restaurantName.isEmpty()
                || address.isEmpty()
                || pickupStart.isEmpty()
                || pickupEnd.isEmpty()
            ) {
                Toast.makeText(baseContext, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Add TOS checkbox check
            // if (!termsCheckbox.isChecked) {
            //     Toast.makeText(baseContext, "You must agree to the terms", Toast.LENGTH_SHORT).show()
            //     return@setOnClickListener
            // }

            // TODO: Integrate this with Firebase
            val registrationInfo = UserRegistrationInfo(
                accountType = AccountType.BUSINESS,
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName,
                restaurant = RestaurantRegistrationInfo(
                    name = restaurantName,
                    address = address,
                    pickupHours = WeeklyPickupHours( // TODO: Allow users to select pickup hours per-day
                        DailyPickupHours(pickupStart, pickupEnd),
                        DailyPickupHours(pickupStart, pickupEnd),
                        DailyPickupHours(pickupStart, pickupEnd),
                        DailyPickupHours(pickupStart, pickupEnd),
                        DailyPickupHours(pickupStart, pickupEnd),
                        DailyPickupHours(pickupStart, pickupEnd),
                        DailyPickupHours(pickupStart, pickupEnd)
                    ),
                    bagsAvailable = 0, // TODO: un-hardcode
                    bagPrice = 6.99    // TODO: un-hardcode
                )
            )

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "Sign up successful!", Toast.LENGTH_SHORT).show()
                        startActivityEmptyIntent(RestaurantSearchView::class.java)
                        finish()
                    } else {
                        Toast.makeText(baseContext, task.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}