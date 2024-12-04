package com.cs407.secondserve

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.LayoutInflater
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
import android.widget.ScrollView
import android.view.Gravity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginView : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_log_in)

        // Get reference to RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.loginRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inflate the login item layout directly without an adapter
        val loginView = LayoutInflater.from(this).inflate(R.layout.login_item, recyclerView, false)

        // Optionally, you can set up the elements directly here, e.g. add text to EditText
        val emailEditText: TextInputEditText = loginView.findViewById(R.id.emailEditText)
        val passwordEditText: TextInputEditText = loginView.findViewById(R.id.passwordEditText)
        val loginButton: MaterialButton = loginView.findViewById(R.id.loginButton)

        // Set up login button click listener
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Handle login logic here
            Toast.makeText(this, "Email: $email, Password: $password", Toast.LENGTH_SHORT).show()
        }

        // Add the loginView to the RecyclerView
        recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                return object : RecyclerView.ViewHolder(loginView) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                // No data to bind here
            }

            override fun getItemCount(): Int {
                return 1  // Since we're only showing one item
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

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                navigateToSignUp()
//            } else {
//                // TODO: User denied our request for location. Need to figure out how to handle this.
//            }
//        }
//    }

    private fun navigateToSignUp() {
        val intent = Intent(this, UserSignUpView::class.java)
        startActivity(intent)
    }

}
