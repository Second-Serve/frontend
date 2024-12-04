package com.cs407.secondserve

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cs407.secondserve.model.Restaurant
import com.cs407.secondserve.service.RestaurantService
import java.util.Calendar
import android.Manifest
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.Location
import android.util.Log
import java.util.Locale

class RestaurantSearchView : SecondServeView() {
    private val location_permission_code = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocation: Location? = null
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

        RestaurantService.fetchAll(
            onSuccess = { restaurants ->
                updateRestaurants(restaurants)
            },
            onFailure = { exception ->
                Toast.makeText(baseContext, exception.message, Toast.LENGTH_LONG).show()
            }
        )

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getUserLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun updateRestaurants(newRestaurants: List<Restaurant>) {
        if (userLocation == null) {
            Log.w(TAG, "Can't update restaurant distances, user has no location.")
        }

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

            val restaurantBagPriceLabel = itemView.findViewById<TextView>(R.id.list_restaurant_bag_price)
            restaurantBagPriceLabel.text = getString(R.string.restaurant_bag_price, restaurant.bagPrice)

            val restaurantBagCountLabel = itemView.findViewById<TextView>(R.id.list_restaurant_bag_count)
            restaurantBagCountLabel.text = getString(R.string.restaurant_bag_count, restaurant.bagsAvailable)

            val distanceTextView = itemView.findViewById<TextView>(R.id.list_restaurant_distance)
            if (userLocation != null) {
                val distance = calculateDistance(
                    userLocation!!,
                    restaurant.location.latitude,
                    restaurant.location.longitude
                )

                distanceTextView.text = if (distance >= 0) {
                    getString(R.string.restaurant_distance_km, distance)
                } else {
                    getString(R.string.location_unavailable)
                }
            } else {
                distanceTextView.text = getString(R.string.location_unavailable)
            }

            val addToCartButton = itemView.findViewById<Button>(R.id.list_restaurant_add_to_cart_button)
            addToCartButton.setOnClickListener {
                val intent = Intent(this, RestaurantPageView::class.java).apply {
                    putExtra("restaurantName", restaurant.name)
                    putExtra("restaurantBagPrice", restaurant.bagPrice)
                    putExtra("restaurantBagCount", restaurant.bagsAvailable)
                    putExtra("restaurantPickupStart", pickupHoursToday.startTime)
                    putExtra("restaurantPickupEnd", pickupHoursToday.endTime)
                    putExtra("restaurantAddress", "TODO") // TODO: Get address with new geo system
                    putExtra("restaurantBannerImagePath", restaurant.bannerImagePath)
                }
                startActivity(intent)
            }

            restaurantListLayout.addView(itemView)
        }
    }

    private fun requestLocationPermission() {
        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
        {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                location_permission_code
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == location_permission_code){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getUserLocation()
            }
            else{
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUserLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userLocation = location
                    Toast.makeText(
                        this,
                        "Location: ${location.latitude}, ${location.longitude}",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Reverse geocode the user's address (optional)
                    val geocoder = Geocoder(this, Locale.getDefault())
                    try {
                        val addresses = geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1
                        )

                        if (!addresses.isNullOrEmpty()) {
                            val userAddress = addresses[0].getAddressLine(0)
                            Log.d("UserLocation", "Address: $userAddress")
                            Toast.makeText(this, "Address: $userAddress", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Log.e("GeocodingError", "Failed to geocode: ${e.message}")
                    }
                } else {
                    Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Log.e("LocationError", "Error fetching location: ${e.message}")
                Toast.makeText(this, "Error fetching location: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun calculateDistance(userLocation: Location, restaurantLat: Double, restaurantLng: Double): Float{
        val restaurantLocation = Location("").apply {
            latitude = restaurantLat
            longitude = restaurantLng
        }
        return userLocation.distanceTo(restaurantLocation) / 1000
    }
}