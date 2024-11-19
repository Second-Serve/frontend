package com.cs407.secondserve

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.VolleyError

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UserAPI.init(this)

        val prefs = getSharedPreferences("com.cs407.secondserve", Context.MODE_PRIVATE)
        val savedEmail = prefs.getString(getString(R.string.saved_email_key), null)
        val savedPassword = prefs.getString(getString(R.string.saved_password_key), null)

        // If we have saved credentials, try logging in with them.
        if (savedEmail != null && savedPassword != null) {
            UserAPI.login(
                savedEmail,
                savedPassword,
                onSuccess = { _ ->
                    // Our saved credentials worked, load the search page.
                    loadRestaurantSearch()
                },
                onError = { _: VolleyError, _: String ->
                    // Something bad happened while logging in (probably bad credentials). Load the
                    // login page instead.
                    loadLogIn()
                }
            )
        } else {
            // We don't have any saved credentials. Load the landing page like normal.
            initializeActivity()
        }
    }

    private fun initializeActivity() {
        setContentView(R.layout.activity_main)

        val userLogInButton: Button = findViewById(R.id.button)
        val userSignUpButton: Button = findViewById(R.id.button2)

        userSignUpButton.setOnClickListener { loadSignUp() }
        userLogInButton.setOnClickListener { loadLogIn() }
    }

    private fun loadSignUp() {
        val intent = Intent(this, GetStarted::class.java)
        startActivity(intent)
    }

    private fun loadLogIn() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun loadRestaurantSearch() {
        val intent = Intent(this, RestaurantSearch::class.java)
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
    }
}