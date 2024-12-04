package com.cs407.secondserve

import android.content.Context
import android.content.Intent
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cs407.secondserve.model.Restaurant
import java.util.Calendar

class RestaurantAdapter(
    private val onRestaurantClick: (Restaurant) -> Unit = {}
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    private var restaurants: List<Restaurant> = listOf()
    private var userLocation: Location? = null

    class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.list_restaurant_name)
        val pickupHoursTextView: TextView = view.findViewById(R.id.list_restaurant_pickup_hours)
        val bagPriceTextView: TextView = view.findViewById(R.id.list_restaurant_bag_price)
        val bagCountTextView: TextView = view.findViewById(R.id.list_restaurant_bag_count)
        val distanceTextView: TextView = view.findViewById(R.id.list_restaurant_distance)
        val addToCartButton: Button = view.findViewById(R.id.list_restaurant_add_to_cart_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_list_item, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurants[position]
        val context = holder.itemView.context

        holder.nameTextView.text = restaurant.name

        // Pickup hours
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val pickupHoursToday = restaurant.pickupHours.onDay(currentDayOfWeek)

        holder.pickupHoursTextView.text = context.getString(
            R.string.restaurant_pickup_hours,
            pickupHoursToday.startTime,
            pickupHoursToday.endTime
        )

        // Bag price
        holder.bagPriceTextView.text = context.getString(
            R.string.restaurant_bag_price,
            restaurant.bagPrice
        )

        // Bag count
        holder.bagCountTextView.text = context.getString(
            R.string.restaurant_bag_count,
            restaurant.bagsAvailable
        )

        // Distance
        if (userLocation != null) {
            val distance = calculateDistance(
                userLocation!!,
                restaurant.location.latitude,
                restaurant.location.longitude
            )

            holder.distanceTextView.text = if (distance >= 0) {
                context.getString(R.string.restaurant_distance_km, distance)
            } else {
                context.getString(R.string.location_unavailable)
            }
        } else {
            holder.distanceTextView.text = context.getString(R.string.location_unavailable)
        }

        // Add to cart button
        holder.addToCartButton.setOnClickListener {
            onRestaurantClick(restaurant)
        }
    }

    override fun getItemCount(): Int = restaurants.size

    fun updateRestaurants(newRestaurants: List<Restaurant>) {
        restaurants = newRestaurants
        notifyDataSetChanged()
    }

    fun updateUserLocation(location: Location?) {
        userLocation = location
        notifyDataSetChanged()
    }

    private fun calculateDistance(userLocation: Location, restaurantLat: Double, restaurantLng: Double): Float {
        val restaurantLocation = Location("").apply {
            latitude = restaurantLat
            longitude = restaurantLng
        }
        return userLocation.distanceTo(restaurantLocation) / 1000
    }
}