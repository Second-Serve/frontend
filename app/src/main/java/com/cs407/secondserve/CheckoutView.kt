package com.cs407.secondserve

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.cs407.secondserve.model.Cart
import com.cs407.secondserve.model.CartItem

class CheckoutView : SecondServeView() {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var totalPriceText: TextView
    private lateinit var checkoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.checkout)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        totalPriceText = findViewById(R.id.total_price_text)
        checkoutButton = findViewById(R.id.checkout_button)

        val cartAdapter = CartAdapter {
            calculateTotalPrice()
        }
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = cartAdapter

        calculateTotalPrice()

        checkoutButton.setOnClickListener {
            if (Cart.getItems().isEmpty()) {
                showToast("Your cart is empty!")
            } else {
                val intent = Intent(this, PaymentView::class.java)
                startActivity(intent)
            }
        }
    }

    private fun calculateTotalPrice() {
        val totalPrice = Cart.getItems().sumOf { it.costPerBag * it.quantity }
        totalPriceText.text = "Total: $${"%.2f".format(totalPrice)}"
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}