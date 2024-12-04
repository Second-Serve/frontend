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
import com.google.firebase.auth.FirebaseAuth

class LoginView : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_log_in)

        val recyclerView: RecyclerView = findViewById(R.id.loginRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val loginView = LayoutInflater.from(this).inflate(R.layout.login_item, recyclerView, false)

        val emailEditText: TextInputEditText = loginView.findViewById(R.id.emailEditText)
        val passwordEditText: TextInputEditText = loginView.findViewById(R.id.passwordEditText)
        val loginButton: MaterialButton = loginView.findViewById(R.id.loginButton)

        val signUpTextView: TextView = loginView.findViewById(R.id.signUpTextView)
        signUpTextView.setOnClickListener {
            val intent = Intent(this, GetStartedView::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            tryLogIn(email, password)
        }

        recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                return object : RecyclerView.ViewHolder(loginView) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            }

            override fun getItemCount(): Int {
                return 1
            }
        }
    }


    object AccountService {
        fun signIn(
            context: android.content.Context, // Add context parameter
            email: String,
            password: String,
            onSuccess: (AuthResult) -> Unit,


            onFailure: (Exception) -> Unit = { exception ->
                exception.printStackTrace() // Log the exception
                Toast.makeText(
                    context, // Use the passed context
                    "Authentication failed: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }


        ) {
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onSuccess(task.result!!)
                    } else {
                        onFailure(task.exception ?: Exception("Unknown error occurred"))
                    }
                }
        }
    }



    private fun tryLogIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        AccountService.signIn(
            this,
            email,
            password,
            onSuccess = { authResult: AuthResult ->
                val user = authResult.user
                if (user != null && user.isEmailVerified) {
                    val intent = Intent(this, RestaurantSearchView::class.java)
                    startActivity(intent)
                } else if (user != null && !user.isEmailVerified) {
                    Toast.makeText(
                        this,
                        "Please verify your email before logging in.",
                        Toast.LENGTH_SHORT
                    ).show()

                    loginButton.isEnabled = false

                    user.sendEmailVerification()
                        .addOnCompleteListener { task ->
                            loginButton.isEnabled = true
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Verification email sent. Please check your inbox.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Failed to send verification email. Please try again later.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { exception ->
                Toast.makeText(
                    this,
                    "Authentication failed: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                navigateToSignUp()
            } else {
                Toast.makeText(this, "Location permission is required for this feature.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToSignUp() {
        val intent = Intent(this, UserSignUpView::class.java)
        startActivity(intent)
    }

}