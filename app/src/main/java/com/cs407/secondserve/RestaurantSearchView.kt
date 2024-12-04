package com.cs407.secondserve

import CheckoutView
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
import android.graphics.Rect
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.Location
import android.util.Log
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class RestaurantSearchView : SecondServeView() {
    private val location_permission_code = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocation: Location? = null

    lateinit var restaurantRecyclerView: RecyclerView
    lateinit var restaurants: List<Restaurant>
    lateinit var restaurantAdapter: RestaurantAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_restaurant_search)
        val lockButton: ImageButton = findViewById(R.id.lockButton)

        lockButton.setOnClickListener {
            val intent = Intent(this, CheckoutView::class.java)
            startActivity(intent)
        }

        restaurantRecyclerView = findViewById(R.id.restaurantRecyclerView)
        restaurantRecyclerView.layoutManager = LinearLayoutManager(this)


        restaurantAdapter = RestaurantAdapter { restaurant ->
            val pickupHoursToday = restaurant.pickupHours.onDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
            val intent = Intent(this, RestaurantPageView::class.java).apply {
                putExtra("restaurantName", restaurant.name)
                putExtra("restaurantBagPrice", restaurant.bagPrice)
                putExtra("restaurantBagCount", restaurant.bagsAvailable)
                putExtra("restaurantPickupStart", pickupHoursToday.startTime)
                putExtra("restaurantPickupEnd", pickupHoursToday.endTime)
                putExtra("restaurantAddress", "TODO")
                putExtra("restaurantBannerImagePath", restaurant.bannerImagePath)
            }
            startActivity(intent)
        }
        restaurantRecyclerView.adapter = restaurantAdapter


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
        restaurants = newRestaurants
        restaurantAdapter.updateRestaurants(newRestaurants)
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
    companion object {
        private const val TAG = "RestaurantSearchView"
    }

}