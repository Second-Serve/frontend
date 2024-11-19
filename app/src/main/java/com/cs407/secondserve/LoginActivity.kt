package com.cs407.secondserve

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.VolleyError
import com.cs407.secondserve.model.User

class LoginActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_log_in)

        val logInButton: Button = findViewById(R.id.log_in_button)

        logInButton.setOnClickListener {
            // If we don't have location access, ask for it
            if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }

            val emailField: EditText = findViewById(R.id.login_email_field)
            val passwordField: EditText = findViewById(R.id.login_password_field)

            tryLogIn(emailField.text.toString(), passwordField.text.toString())
        }
    }

    private fun tryLogIn(email: String, password: String) {
        UserAPI.login(
            email,
            password,
            onSuccess = { user: User ->
                UserAPI.user = user

                val intent = Intent(this, RestaurantSearch::class.java)
                startActivity(intent)
            },
            onError = { error: VolleyError, message: String ->
                val messageToDisplay: String

                if (error.networkResponse.statusCode == 400) {
                    messageToDisplay = "Invalid username or password."
                } else {
                    messageToDisplay = message
                }

                // Show the error we just got
                Toast.makeText(this, messageToDisplay, Toast.LENGTH_LONG).show()
            }
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                navigateToSignUp()
            } else {
            }
        }
    }

    private fun navigateToSignUp() {
        val intent = Intent(this, SignUpUser::class.java)
        startActivity(intent)
    }
}

