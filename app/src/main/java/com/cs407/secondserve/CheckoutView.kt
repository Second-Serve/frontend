package com.cs407.secondserve

import android.content.Intent
import android.os.Bundle
import android.widget.Button

class CheckoutView : SecondServeView() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val checkoutButton: Button = findViewById(R.id.checkout_button)

        checkoutButton.setOnClickListener {
            val intent = Intent(this, PaymentView::class.java)
            startActivity(intent)
        }
    }
}