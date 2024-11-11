package com.cs407.secondserve

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue

private const val BASE_URL = "http://<your-backend-ip>:80"

class MainActivity : AppCompatActivity() {

    private lateinit var userApiService: UserApiService

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

        fetchUsersExample()
    }
    private fun fetchUsersExample() {
        userApiService.fetchUsers(
            bearerToken = "your-bearer-token",
            onSuccess = { response ->
                // Handle successful response
                println("Fetched users: $response")
            },
            onError = { error ->
                // Handle error
                println("Error fetching users: $error")
            }
        )
    }

    override fun onStop() {
        super.onStop()
        userApiService.cancelAllRequests()
    }
}