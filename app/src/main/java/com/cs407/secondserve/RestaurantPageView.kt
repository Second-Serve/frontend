package com.cs407.secondserve

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
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
import android.net.Uri
import android.widget.Toast
import com.bumptech.glide.Glide

class RestaurantPageView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_restaurant_page)

        val lockButton: ImageButton = findViewById(R.id.lockButton)
        lockButton.setOnClickListener {
            val intent = Intent(this, CheckoutView::class.java)
            startActivity(intent)
        }

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val addToCartButton = findViewById<Button>(R.id.addToCartButton)
        val extras = intent.extras
        if (extras == null) {
            Snackbar.make(
                findViewById(android.R.id.content),
                "Restaurant data not found.",
                Snackbar.LENGTH_LONG
            ).show()
            finish()
            return
        }

        // Set data for restaurant details
        val restaurantId = extras.getString("restaurantId")!!
        val restaurantName = extras.getString("restaurantName")!!
        val bagPrice = extras.getDouble("restaurantBagPrice")
        val bagCount = extras.getInt("restaurantBagCount")
        val pickupStart = extras.getString("restaurantPickupStart")!!
        val pickupEnd = extras.getString("restaurantPickupEnd")!!
        val address = extras.getString("restaurantAddress")!!

        findViewById<TextView>(R.id.restaurant_name_text).text = restaurantName
        findViewById<TextView>(R.id.restaurant_price_text).text = getString(
            R.string.restaurant_bag_price, bagPrice
        )
        findViewById<TextView>(R.id.restaurant_bag_count_text).text = getString(
            R.string.restaurant_bag_count, bagCount
        )
        findViewById<TextView>(R.id.restaurant_pickup_text).text = getString(
            R.string.restaurant_pickup_hours, pickupStart, pickupEnd
        )
        findViewById<TextView>(R.id.restaurant_location_text).text = address

        addToCartButton.setOnClickListener {
            val cartItem = CartItem(
                restaurantId = restaurantId,
                restaurantName = restaurantName,
                restaurantAddress = address,
                costPerBag = bagPrice,
                quantity = 1 // TODO: Allow dynamic quantity selection
            )
            Cart.addItemToCart(cartItem)
            Snackbar.make(
                findViewById(android.R.id.content),
                "Item added to cart.",
                Snackbar.LENGTH_SHORT
            ).show()
            finish()
        }
        val bannerImageView: ImageView = findViewById(R.id.restaurant_banner_image)
        bannerImageView.setOnClickListener {
            openAddressInMaps(address)
        }
        fetchAndDisplayBannerImage(restaurantId, bannerImageView)
    }

    /**
     * Fetches and displays the map banner image for the restaurant.
     */
    private fun fetchAndDisplayBannerImage(restaurantId: String, bannerImageView: ImageView) {
        LocationService.getRestaurantMapImage(
            restaurantId = restaurantId,
            type = MapImageType.BANNER,
            onSuccess = { url ->
                runOnUiThread {
                    Glide.with(this).load(url).into(bannerImageView);
                }
            },
            onFailure = { exception ->
                Log.e("RestaurantPageView", "Failed to load map image: ${exception.message}")
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Failed to load map image.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        )
    }

    private fun openAddressInMaps(address: String) {
        try {
            val mapsIntentUri = Uri.parse("https://maps.google.com/maps?daddr=${address}")
            val mapIntent = Intent(Intent.ACTION_VIEW, mapsIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "Google Maps app not found.", Toast.LENGTH_SHORT).show()
        }
    }
}
