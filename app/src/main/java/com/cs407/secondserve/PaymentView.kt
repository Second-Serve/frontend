package com.cs407.secondserve

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import android.text.InputFilter
import android.util.Log
import android.view.View
import com.cs407.secondserve.model.Cart
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import org.json.JSONObject

class PaymentView : AppCompatActivity() {

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

        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        cardNumberEditText = findViewById(R.id.cardNumberEditText)
        expirationDateEditText = findViewById(R.id.expirationDateEditText)
        cvcEditText = findViewById(R.id.cvcEditText)
        zipCodeEditText = findViewById(R.id.zipCodeEditText)
        submitPaymentButton = findViewById(R.id.submitPaymentButton)

        submitPaymentButton.setOnClickListener(this::submitPayment)

        cardNumberEditText.filters = arrayOf(InputFilter.LengthFilter(16))

        cardNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                editable?.let {
                    if (editable.length > 16) {
                        cardNumberEditText.setText(editable.substring(0, 16))
                        cardNumberEditText.setSelection(16)
                    }

                    if (!editable.toString().matches(Regex("^[0-9]*$"))) {
                        cardNumberEditText.setText(editable.toString().filter { it.isDigit() })
                        cardNumberEditText.setSelection(editable.length)
                    }

//                    if (editable.length < 16) {
//                        Toast.makeText(this@PaymentView, "Card number must be 16 digits", Toast.LENGTH_SHORT).show()
//                    }
                }
            }
        })

        cvcEditText.filters = arrayOf(InputFilter.LengthFilter(3))
    }

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

    private fun submitPayment(view: View) {
        if (isFormValid()) {
            val orderData = Cart.toMap()
            Log.d("PaymentView", "Order Data: $orderData")
            Firebase.functions.getHttpsCallable("placeOrder")
                .call(orderData)
                .addOnSuccessListener { result ->
                    val g = Gson()
                    val data = g.fromJson(result.getData().toString(), JSONObject::class.java)
                    Log.d("PaymentView", "Success: $data")
                }
                .addOnFailureListener {
                    Log.d("PaymentView", "Failure: $it")
                }

            Toast.makeText(this, "Your order has been placed!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, PreviousOrdersView::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
        }
    }
}