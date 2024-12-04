package com.cs407.secondserve

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton

class PaymentView : AppCompatActivity() {

    // Declare the input fields and the submit button
    private lateinit var firstNameEditText: TextInputEditText
    private lateinit var lastNameEditText: TextInputEditText
    private lateinit var cardNumberEditText: TextInputEditText
    private lateinit var expirationDateEditText: TextInputEditText
    private lateinit var cvcEditText: TextInputEditText
    private lateinit var zipCodeEditText: TextInputEditText
    private lateinit var submitPaymentButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment)

        // Initialize the input fields and the submit button
        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        cardNumberEditText = findViewById(R.id.cardNumberEditText)
        expirationDateEditText = findViewById(R.id.expirationDateEditText)
        cvcEditText = findViewById(R.id.cvcEditText)
        zipCodeEditText = findViewById(R.id.zipCodeEditText)
        submitPaymentButton = findViewById(R.id.submitPaymentButton)

        // Set click listener for the submit button
        submitPaymentButton.setOnClickListener {
            // Perform validation
            if (isFormValid()) {
                // If all fields are valid, proceed with payment processing (or another action)
                Toast.makeText(this, "Payment Submitted Successfully", Toast.LENGTH_SHORT).show()
            } else {
                // If validation fails, show a message
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to check if all required fields are filled
    private fun isFormValid(): Boolean {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val cardNumber = cardNumberEditText.text.toString().trim()
        val expirationDate = expirationDateEditText.text.toString().trim()
        val cvc = cvcEditText.text.toString().trim()
        val zipCode = zipCodeEditText.text.toString().trim()

        // Return false if any of the fields are empty
        return firstName.isNotEmpty() &&
                lastName.isNotEmpty() &&
                cardNumber.isNotEmpty() &&
                expirationDate.isNotEmpty() &&
                cvc.isNotEmpty() &&
                zipCode.isNotEmpty()
    }
}
