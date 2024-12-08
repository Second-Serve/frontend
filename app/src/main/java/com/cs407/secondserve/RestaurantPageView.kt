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
        fetchAndDisplayBannerImage(restaurantId, bannerImageView)
    }

    /**
     * Fetches and displays the map banner image for the restaurant.
     */
    private fun fetchAndDisplayBannerImage(restaurantId: String, bannerImageView: ImageView) {
        LocationService.getRestaurantMapImage(
            restaurantId = restaurantId,
            type = MapImageType.BANNER,
            onSuccess = { mapImageBase64 ->
                val mapBitmap = decodeBase64ToBitmap(mapImageBase64)
                runOnUiThread {
                    bannerImageView.setImageBitmap(mapBitmap)
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


    private fun decodeBase64ToBitmap(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}
