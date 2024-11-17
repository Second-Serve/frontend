package com.cs407.secondserve

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.cs407.secondserve.model.AccountType
import com.cs407.secondserve.model.RestaurantRegistrationInfo
import com.cs407.secondserve.model.UserRegistrationInfo
import com.cs407.secondserve.model.WeeklyPickupHours

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton: Button = findViewById(R.id.button)
        val signUpButton: Button = findViewById(R.id.button2)

        signUpButton.setOnClickListener {
            val intent = Intent(this, GetStarted::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val api = UserAPI(this)
        val registrationInfo = UserRegistrationInfo(
            accountType = AccountType.BUSINESS,
            email = "ozinn@wisc.edu",
            password = "password123",
            firstName = "Owen",
            lastName = "Zinn",
            restaurant = RestaurantRegistrationInfo(
                name = "Owen's Restaurant",
                address = "1234 Fake St",
                pickupHours = WeeklyPickupHours.ALWAYS
            )
        )

        println(registrationInfo.toJSONObject())

        api.registerAccount(
            registrationInfo,
            onSuccess = { user ->
                println(user)
            },
            onError = { error, message ->
                println(message)
            }
        )
    }

    override fun onStop() {
        super.onStop()
    }
}