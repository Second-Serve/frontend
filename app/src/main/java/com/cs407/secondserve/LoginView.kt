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
import com.cs407.secondserve.service.AccountService
import com.google.firebase.auth.AuthResult

class LoginView : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    private lateinit var loginEmailField: EditText
    private lateinit var loginPasswordField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_log_in)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val loginLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
            )
        }

        val logInTitle = TextView(this).apply {
            text = getString(R.string.log_in_title)
            textSize = 36f
            setTextColor(resources.getColor(R.color.accent, theme))
        }
        loginLayout.addView(logInTitle)

        val loginEmailLabel = TextView(this).apply {
            text = getString(R.string.email_label)
            textSize = 18f
            setTextColor(resources.getColor(R.color.accent, theme))
        }
        loginLayout.addView(loginEmailLabel)

        loginEmailField = EditText(this).apply {
            id = View.generateViewId()
            hint = getString(R.string.enter_email_hint)
            setBackgroundResource(R.drawable.rounded_border)
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        loginLayout.addView(loginEmailField)

        val loginPasswordLabel = TextView(this).apply {
            text = getString(R.string.password_label)
            textSize = 18f
            setTextColor(resources.getColor(R.color.accent, theme))
        }
        loginLayout.addView(loginPasswordLabel)

        loginPasswordField = EditText(this).apply {
            id = View.generateViewId()
            hint = getString(R.string.enter_password_hint)
            setBackgroundResource(R.drawable.rounded_border)
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        loginLayout.addView(loginPasswordField)

        val logInButton = Button(this).apply {
            text = getString(R.string.log_in_button)
            setBackgroundColor(resources.getColor(R.color.accent, theme))
            setTextColor(resources.getColor(android.R.color.white, theme))
        }
        loginLayout.addView(logInButton)

        recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return object : RecyclerView.ViewHolder(loginLayout) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

            override fun getItemCount(): Int = 1
        }

        logInButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            } else {
                tryLogIn(loginEmailField.text.toString(), loginPasswordField.text.toString())
            }
        }
    }

    private fun tryLogIn(email: String, password: String) {


        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        AccountService.signIn(
            email,
            password,
            onSuccess = { authResult: AuthResult ->
                val user = authResult.user
                if (user != null) {
                    val intent = Intent(this, RestaurantSearchView::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show()
                }
            },

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
        val intent = Intent(this, UserSignUpView::class.java)
        startActivity(intent)
    }
}

