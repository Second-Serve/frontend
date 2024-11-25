package com.cs407.secondserve

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.cs407.secondserve.model.Restaurant
import java.util.Calendar


class RestaurantSearchView : SecondServeView() {
    lateinit var restaurantListLayout: LinearLayout

    lateinit var restaurants: List<Restaurant>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_restaurant_search)

        restaurantListLayout = findViewById(R.id.restaurant_list)

        // When you hit the back arrow, go back
        val backArrow = findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener {
            finish()
        }

        // TODO: Update to use Firebase
//        UserAPI.fetchRestaurants(
//            onSuccess = { fetchedRestaurants: List<Restaurant> ->
//                updateRestaurants(fetchedRestaurants)
//            },
//            onError = { _: VolleyError, _: String ->
//                restaurantListLayout.removeAllViews()
//                Toast.makeText(baseContext, R.string.error_cannot_get_restaurants, Toast.LENGTH_SHORT).show()
//            }
//        )
    }

    private fun updateRestaurants(newRestaurants: List<Restaurant>) {
        restaurants = newRestaurants

        restaurantListLayout.removeAllViews()

        val inflater = LayoutInflater.from(this)

        for (restaurant in restaurants) {
            val itemView: View = inflater.inflate(R.layout.restaurant_list_item, restaurantListLayout, false)

            // Restaurant name
            val restaurantNameLabel = itemView.findViewById<TextView>(R.id.list_restaurant_name)
            restaurantNameLabel.text = restaurant.name

            // Pickup hours
            val restaurantPickupHoursLabel = itemView.findViewById<TextView>(R.id.list_restaurant_pickup_hours)
            val calendar = Calendar.getInstance()
            val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val pickupHoursToday = restaurant.pickupHours.onDay(currentDayOfWeek)
            restaurantPickupHoursLabel.text = getString(
                R.string.restaurant_pickup_hours,
                pickupHoursToday.startTime,
                pickupHoursToday.endTime
            )

            // TODO: Remove hard-coding
            val restaurantBagPriceLabel = itemView.findViewById<TextView>(R.id.list_restaurant_bag_price)
            restaurantBagPriceLabel.text = getString(R.string.restaurant_bag_price, 6.99)

            // TODO: Remove hard-coding
            val restaurantBagCountLabel = itemView.findViewById<TextView>(R.id.list_restaurant_bag_count)
            restaurantBagCountLabel.text = getString(R.string.restaurant_bag_count, 4)

            // Go to the restaurant page when "Add to Cart" clicked
            val addToCartButton = itemView.findViewById<Button>(R.id.list_restaurant_add_to_cart_button)
            addToCartButton.setOnClickListener {
                val intent = Intent(this, RestaurantPageView::class.java)
                intent.putExtra("restaurantName", restaurant.name)
                intent.putExtra("restaurantBagPrice", 6.99) // TODO: un-hardcode
                intent.putExtra("restaurantBagCount", 4) // TODO: un-hardcode
                intent.putExtra("restaurantPickupStart", pickupHoursToday.startTime)
                intent.putExtra("restaurantPickupEnd", pickupHoursToday.endTime)
                intent.putExtra("restaurantAddress", restaurant.address)
                intent.putExtra("restaurantBannerImagePath", restaurant.bannerImagePath)
                startActivity(intent)
            }

            restaurantListLayout.addView(itemView)
        }
    }
}