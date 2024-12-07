package com.cs407.secondserve

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CheckoutView : SecondServeView() {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var totalPriceText: TextView
    private lateinit var checkoutButton: Button
    private val cartItems = mutableListOf<List<Any>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.checkout)

        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        totalPriceText = findViewById(R.id.total_price_text)
        checkoutButton = findViewById(R.id.checkout_button)

        // Setup RecyclerView
        val cartAdapter = CartAdapter()
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = cartAdapter

        cartAdapter.updateCart()
        // Calculate Total Price
//        val totalPrice = cartItems.sumOf { it.price * it.quantity }
//        totalPriceText.text = "Total: $${"%.2f".format(totalPrice)}"

        // Checkout Button Click Listener

        checkoutButton.setOnClickListener {
            val intent = Intent(this, PaymentView::class.java)
            startActivity(intent)
        }



    }
}