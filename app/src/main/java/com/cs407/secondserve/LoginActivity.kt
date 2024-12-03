package com.cs407.secondserve

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.cs407.secondserve.model.User

class LoginActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_log_in)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val loginLayout = LinearLayout(this)
        loginLayout.orientation = LinearLayout.VERTICAL
        loginLayout.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
        )

        val logInTitle = TextView(this)
        logInTitle.text = getString(R.string.log_in_title)
        logInTitle.textSize = 36f
        logInTitle.setTextColor(resources.getColor(R.color.accent))
        loginLayout.addView(logInTitle)

        val loginEmailLabel = TextView(this)
        loginEmailLabel.text = getString(R.string.email_label)
        loginEmailLabel.textSize = 18f
        loginEmailLabel.setTextColor(resources.getColor(R.color.accent))
        loginLayout.addView(loginEmailLabel)

        val loginEmailField = EditText(this)
        loginEmailField.id = View.generateViewId()
        loginEmailField.hint = getString(R.string.enter_email_hint)
        loginEmailField.setBackgroundResource(R.drawable.rounded_border)
        loginEmailField.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        loginLayout.addView(loginEmailField)

        val loginPasswordLabel = TextView(this)
        loginPasswordLabel.text = getString(R.string.password_label)
        loginPasswordLabel.textSize = 18f
        loginPasswordLabel.setTextColor(resources.getColor(R.color.accent))
        loginLayout.addView(loginPasswordLabel)

        val loginPasswordField = EditText(this)
        loginPasswordField.id = View.generateViewId()
        loginPasswordField.hint = getString(R.string.enter_password_hint)
        loginPasswordField.setBackgroundResource(R.drawable.rounded_border)
        loginPasswordField.inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        loginLayout.addView(loginPasswordField)

        val logInButton = Button(this)
        logInButton.text = getString(R.string.log_in_button)
        logInButton.setBackgroundColor(resources.getColor(R.color.accent))
        logInButton.setTextColor(resources.getColor(android.R.color.white))
        loginLayout.addView(logInButton)

        recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return object : RecyclerView.ViewHolder(loginLayout) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

            override fun getItemCount(): Int {
                return 1
            }
        }
        logInButton.setOnClickListener {
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

            val emailField = loginEmailField
            val passwordField = loginPasswordField

            tryLogIn(emailField.text.toString(), passwordField.text.toString())
        }
    }

    private fun tryLogIn(email: String, password: String) {
        UserAPI.login(
            email,
            password,
            onSuccess = { _: User ->
                UserAPI.saveUser(applicationContext)

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
                // TODO: User denied our request for location. Need to figure out how to handle this.
            }
        }
    }

    private fun navigateToSignUp() {
        val intent = Intent(this, SignUpUser::class.java)
        startActivity(intent)
    }
}

