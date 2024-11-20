package com.cs407.secondserve

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RestaurantPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_restaurant_page)

        val backArrow = findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener {
            finish()
        }

        // Populate text with restaurant info
        val extras = intent.extras
        if (extras != null) {
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
}