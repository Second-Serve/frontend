package com.cs407.secondserve

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

data class UserRegistrationInfo(
    val email: String,
    val password: String,
    val accountType: String
)

class SignUpUser : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_sign_up_user)

        val emailField: EditText = findViewById(R.id.email_input)
        val passwordField: EditText = findViewById(R.id.password_input)
        val confirmPasswordField: EditText = findViewById(R.id.confirm_password_input)
        val termsCheckbox: CheckBox = findViewById(R.id.terms_checkbox)
        val signUpButton: Button = findViewById(R.id.sign_up_button)

        signUpButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val confirmPassword = confirmPasswordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!termsCheckbox.isChecked) {
                Toast.makeText(this, "You must agree to the terms", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userInfo = UserRegistrationInfo(
                email = email,
                password = password,
                accountType = "CUSTOMER"
            )

            val userJson = JSONObject().apply {
                put("email", userInfo.email)
                put("password", userInfo.password)
                put("accountType", userInfo.accountType)
            }

//            userApiService.createAccount(
//                userInfo = userJson,
//                onSuccess = {
//                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
//                    finish()
//                },
//                onError = { error ->
//                    Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
//                }
//            )
        }
    }

    override fun onStop() {
        super.onStop()
//        userApiService.cancelAllRequests()
    }
}
