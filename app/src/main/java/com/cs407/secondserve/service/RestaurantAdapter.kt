package com.cs407.secondserve

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cs407.secondserve.model.Restaurant
import com.cs407.secondserve.service.LocationService
import java.util.Calendar

class RestaurantAdapter(
    private val onRestaurantClick: (Restaurant) -> Unit = {}
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    private var restaurants: List<Restaurant> = listOf()
    private lateinit var view: View

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
        val pickupHoursToday = restaurant.pickupHours.onDay(currentDayOfWeek - 1)

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
        val bagsRemaining = restaurant.bagsAvailable - restaurant.bagsClaimed
        holder.bagCountTextView.text = context.getString(
            R.string.restaurant_bag_count,
            bagsRemaining
        )

        // Add to cart button
        holder.addToCartButton.setOnClickListener {
            onRestaurantClick(restaurant)
        }

        // Distance
        LocationService.getDistanceToRestaurant(
            restaurant.id,
            onSuccess = { distance ->
                Log.d("RestaurantAdapter", "Distance to restaurant: $distance")
                holder.distanceTextView.text = if (distance >= 0) {
                    context.getString(R.string.restaurant_distance_mi, distance)
                } else {
                    context.getString(R.string.location_unavailable)
                }
            }
        )
    }

    override fun getItemCount(): Int = restaurants.size

    fun updateRestaurants(newRestaurants: List<Restaurant>) {
        restaurants = newRestaurants
        notifyDataSetChanged()
    }
}