package com.cs407.secondserve

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.VolleyError
import com.cs407.secondserve.model.Restaurant
import java.util.Calendar


class RestaurantSearch : AppCompatActivity() {
    lateinit var restaurantListLayout: LinearLayout

    lateinit var restaurants: List<Restaurant>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_restaurant_search)

        restaurantListLayout = findViewById(R.id.restaurant_list)

        // When you hit the back arrow, go back
        val backArrow = findViewById<ImageView>(R.id.restaurant_search_back_arrow)
        backArrow.setOnClickListener {
            finish()
        }

        UserAPI.fetchRestaurants(
            onSuccess = { fetchedRestaurants: List<Restaurant> ->
                updateRestaurants(fetchedRestaurants)
            },
            onError = { _: VolleyError, _: String ->
                restaurantListLayout.removeAllViews()
                Toast.makeText(this, R.string.error_cannot_get_restaurants, Toast.LENGTH_SHORT).show()
            }
        )
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
                R.string.list_restaurant_pickup_hours,
                pickupHoursToday.startTime,
                pickupHoursToday.endTime
            )

            // TODO: Price and bag count

            restaurantListLayout.addView(itemView)
        }
    }
}