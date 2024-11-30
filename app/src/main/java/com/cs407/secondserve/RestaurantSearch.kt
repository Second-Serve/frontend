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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.VolleyError
import com.cs407.secondserve.model.Restaurant
import java.util.Calendar
import android.Manifest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.Location
import android.util.Log

class RestaurantSearch : AppCompatActivity() {

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

            val restaurantNameLabel = itemView.findViewById<TextView>(R.id.list_restaurant_name)
            restaurantNameLabel.text = restaurant.name


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
            restaurantBagPriceLabel.text = getString(R.string.restaurant_bag_price, 6.99)

            val restaurantBagCountLabel = itemView.findViewById<TextView>(R.id.list_restaurant_bag_count)
            restaurantBagCountLabel.text = getString(R.string.restaurant_bag_count, 4)


            val distanceTextView = itemView.findViewById<TextView>(R.id.list_restaurant_distance)
            val distance = if (userLocation != null) {
                calculateDistance(userLocation!!, restaurant.latitude, restaurant.longitude)
            } else {
                -1f
            }


            distanceTextView.text = if (distance >= 0) {
                String.format("%.2f km away", distance) // Format distance
            } else {
                "Location unavailable"
            }


            val addToCartButton = itemView.findViewById<Button>(R.id.list_restaurant_add_to_cart_button)
            addToCartButton.setOnClickListener {
                val intent = Intent(this, RestaurantPage::class.java)
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

    private fun requestLocationPermission(){
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
        if(ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION

        ) == PackageManager.PERMISSION_GRANTED
            )
        {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                location->
                if(location != null){
                    userLocation = location
                    Toast.makeText(this, "Location: ${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show()
                }
            }

        }
        else{
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