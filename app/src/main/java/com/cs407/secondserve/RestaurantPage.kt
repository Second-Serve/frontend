package com.cs407.secondserve

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.ImageLoader.ImageCache
import com.android.volley.toolbox.NetworkImageView
import com.cs407.secondserve.util.AppImageCache


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

            // Banner
            val bannerImage = findViewById<NetworkImageView>(R.id.restaurant_banner_image)
            bannerImage.setDefaultImageResId(R.drawable.baseline_restaurant_menu_24)
            bannerImage.setImageUrl(
                extras.getString("restaurantBannerImagePath"),
                UserAPI.imageLoader
            )
            println("set image url")
        }
    }
}