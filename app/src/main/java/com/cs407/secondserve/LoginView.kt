package com.cs407.secondserve

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs407.secondserve.service.AccountService
import com.cs407.secondserve.service.LocationService
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.cs407.secondserve.model.AccountType

class LoginView : AppCompatActivity() {
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_log_in)

        LocationService.requestLocation(this)

        val recyclerView: RecyclerView = findViewById(R.id.loginRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val loginView = LayoutInflater.from(this).inflate(R.layout.login_item, recyclerView, false)
        val emailEditText: TextInputEditText = loginView.findViewById(R.id.emailEditText)
        val passwordEditText: TextInputEditText = loginView.findViewById(R.id.passwordEditText)
        loginButton = loginView.findViewById(R.id.loginButton)

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

    private fun tryLogIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        AccountService.signIn(
            email,
            password,
            onSuccess = { authResult, user ->
                val authUser = authResult.user

                if (authUser == null) {
                    Toast.makeText(this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show()
                    return@signIn
                }

                if (user.accountType == AccountType.BUSINESS) {
                    val intent = Intent(this, RestaurantMainView::class.java)
                    startActivity(intent)
                    return@signIn
                } else if (authUser.isEmailVerified) {
                    val intent = Intent(this, RestaurantSearchView::class.java)
                    startActivity(intent)
                    return@signIn
                }

                Toast.makeText(
                    this,
                    "Please verify your email before logging in.",
                    Toast.LENGTH_SHORT
                ).show()

                loginButton.isEnabled = false

                authUser.sendEmailVerification()
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


    private fun navigateToSignUp() {
        val intent = Intent(this, UserSignUpView::class.java)
        startActivity(intent)
    }

}