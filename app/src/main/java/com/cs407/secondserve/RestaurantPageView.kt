package com.cs407.secondserve

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cs407.secondserve.model.Cart
import com.cs407.secondserve.model.CartItem
import com.cs407.secondserve.model.MapImageType
import com.cs407.secondserve.service.LocationService
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class RestaurantPageView : SecondServeView() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_restaurant_page)

        // Initialize toolbar
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val extras = intent.extras!!
        val restaurantId = extras.getString("restaurantId")!!

        // Set restaurant details
        findViewById<TextView>(R.id.restaurant_name_text).text = extras.getString("restaurantName")
        findViewById<TextView>(R.id.restaurant_price_text).text = getString(
            R.string.restaurant_bag_price,
            extras.getDouble("restaurantBagPrice")
        )
        findViewById<TextView>(R.id.restaurant_bag_count_text).text = getString(
            R.string.restaurant_bag_count,
            extras.getInt("restaurantBagCount")
        )
        findViewById<TextView>(R.id.restaurant_pickup_text).text = getString(
            R.string.restaurant_pickup_hours,
            extras.getString("restaurantPickupStart"),
            extras.getString("restaurantPickupEnd")
        )
        findViewById<TextView>(R.id.restaurant_location_text).text = extras.getString("restaurantAddress")

        // Fetch and display banner image
        val bannerImageView: ImageView = findViewById(R.id.restaurant_banner_image)
        fetchAndDisplayBannerImage(restaurantId, bannerImageView)

        // Handle add to cart button click
        findViewById<ImageButton>(R.id.lockButton).setOnClickListener {
            val cartItem = CartItem(
                restaurantId = restaurantId,
                restaurantName = extras.getString("restaurantName")!!,
                restaurantAddress = extras.getString("restaurantAddress")!!,
                costPerBag = extras.getDouble("restaurantBagPrice"),
                quantity = 1
            )
            Cart.addItemToCart(cartItem)
            Snackbar.make(it, "Added to cart", Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * Fetches the map banner image for the restaurant and displays it in the provided ImageView.
     */
    private fun fetchAndDisplayBannerImage(restaurantId: String, bannerImageView: ImageView) {
        LocationService.getRestaurantMapImage(
            restaurantId = restaurantId,
            type = MapImageType.BANNER,
            onSuccess = { bitmap: Bitmap ->
                runOnUiThread {
                    bannerImageView.setImageBitmap(bitmap)
                }
            },
            onFailure = { exception ->
                Log.e("RestaurantPageView", "Failed to load map image: ${exception.message}")
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Failed to load map image.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        )
    }
}