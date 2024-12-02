package com.cs407.secondserve

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cs407.secondserve.service.AccountService

class LoginView : SecondServeView() {
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

            trySignIn(emailField.text.toString(), passwordField.text.toString())
        }
    }

    private fun trySignIn(email: String, password: String) {
        AccountService.signIn(
            email,
            password,
            onSuccess = {
                // We were able to sign in, so go to the search view
                startActivityEmptyIntent(RestaurantSearchView::class.java)
            },
            onFailure = { exception ->
                Toast.makeText(baseContext, exception.message, Toast.LENGTH_LONG).show()
            }
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityEmptyIntent(UserSignUpView::class.java)
            } else {
                // TODO: User denied our request for location. Need to figure out how to handle this.
            }
        }
    }
}

