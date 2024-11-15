package com.cs407.secondserve

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.cs407.secondserve.model.AccountType
import com.cs407.secondserve.model.UserInfo
import com.cs407.secondserve.model.UserRegistrationInfo

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
            UserInfo(
                accountType = AccountType.CUSTOMER,
                email = "ozinn@wisc.edu",
                firstName = "Owen",
                lastName = "Zinn"
            ),
            password = "password123"
        )

        api.registerAccount(registrationInfo) { user ->
            println(user)
        }
    }

    override fun onStop() {
        super.onStop()
    }
}