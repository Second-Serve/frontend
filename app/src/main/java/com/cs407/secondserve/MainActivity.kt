package com.cs407.secondserve

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import org.json.JSONObject

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

//        val api = UserAPI(this)
//        val registrationInfo = UserRegistrationInfo(
//
//        )
//        api.registerAccount(
//            registrationInfo
//        )
    }

    override fun onStop() {
        super.onStop()
    }
}