package com.cs407.secondserve

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.cs407.secondserve.model.Cart
import com.cs407.secondserve.model.CartItem
import com.cs407.secondserve.model.Restaurant

class RestaurantPageView : SecondServeView() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_restaurant_page)

        val lockButton: ImageButton = findViewById(R.id.lockButton)

        lockButton.setOnClickListener {
            val intent = Intent(this, CheckoutView::class.java)
            startActivity(intent)
        }

        val backArrow = findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener {
            finish()
        }

        val addToCartButton = findViewById<Button>(R.id.addToCartButton)
        val extras = intent.extras!!
        addToCartButton.setOnClickListener {
            val cartItem = CartItem(
                restaurantId = extras.getString("restaurantId")!!,
                restaurantName = extras.getString("restaurantName")!!,
                restaurantAddress = extras.getString("restaurantAddress")!!,
                costPerBag = extras.getDouble("restaurantBagPrice"),
                quantity = 1 // TODO: Make this a spinner
            )
            Cart.addItemToCart(cartItem)
            finish()
        }

        // Restaurant name
        findViewById<TextView>(R.id.restaurant_name_text).text = extras.getString("restaurantName")

        // Bag price
        findViewById<TextView>(R.id.restaurant_price_text).text = getString(
            R.string.restaurant_bag_price,
            extras.getDouble("restaurantBagPrice")
        )

        // Bag count
        findViewById<TextView>(R.id.restaurant_bag_count_text).text = getString(
            R.string.restaurant_bag_count,
            extras.getInt("restaurantBagCount")
        )

        // Pickup hours
        findViewById<TextView>(R.id.restaurant_pickup_text).text = getString(
            R.string.restaurant_pickup_hours,
            extras.getString("restaurantPickupStart"),
            extras.getString("restaurantPickupEnd")
        )

        // Address
        findViewById<TextView>(R.id.restaurant_location_text).text = extras.getString("restaurantAddress")

    }
}