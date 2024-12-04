package com.cs407.secondserve

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import android.text.InputFilter

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

        // Limit the card number input to 16 digits (ensure it's numeric)
        cardNumberEditText.filters = arrayOf(InputFilter.LengthFilter(16))

        cardNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                editable?.let {
                    if (editable.length > 16) {
                        // If the length exceeds 16 characters, truncate the input to 16 digits
                        cardNumberEditText.setText(editable.substring(0, 16))
                        cardNumberEditText.setSelection(16)  // Move the cursor to the end
                    }

                    // Ensure the card number contains only digits
                    if (!editable.toString().matches(Regex("^[0-9]*$"))) {
                        cardNumberEditText.setText(editable.toString().filter { it.isDigit() })
                        cardNumberEditText.setSelection(editable.length)
                    }

                    // If the length is less than 16, show a toast reminder
                    if (editable.length < 16) {
                        Toast.makeText(this@PaymentView, "Card number must be 16 digits", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        cvcEditText.filters = arrayOf(InputFilter.LengthFilter(3))

        expirationDateEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                editable?.let {
                    val length = editable.length
                    if (length == 2 && editable.toString()[1] != '/') {
                        expirationDateEditText.setText("${editable.substring(0, 2)}/")
                        expirationDateEditText.setSelection(3)
                    } else if (length > 5) {
                        expirationDateEditText.setText(editable.substring(0, 5))
                        expirationDateEditText.setSelection(5)
                    }
                }
            }
        })
    }

    // Function to check if all required fields are filled
    private fun isFormValid(): Boolean {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val cardNumber = cardNumberEditText.text.toString().trim()
        val expirationDate = expirationDateEditText.text.toString().trim()
        val cvc = cvcEditText.text.toString().trim()
        val zipCode = zipCodeEditText.text.toString().trim()

        return firstName.isNotEmpty() &&
                lastName.isNotEmpty() &&
                cardNumber.length == 16 &&
                expirationDate.isNotEmpty() &&
                cvc.isNotEmpty() &&
                zipCode.isNotEmpty()
    }

}
