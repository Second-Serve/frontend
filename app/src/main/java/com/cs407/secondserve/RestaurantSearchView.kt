package com.cs407.secondserve

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
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
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
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

        val viewOrdersButton: Button = findViewById(R.id.view_previous_orders_button)
        viewOrdersButton.setOnClickListener {
            val intent = Intent(this, CheckoutView::class.java)
            startActivity(intent)
        }

        restaurantRecyclerView = findViewById(R.id.restaurantRecyclerView)
        restaurantRecyclerView.layoutManager = LinearLayoutManager(this)

        // Adapter with validation logic added
        restaurantAdapter = RestaurantAdapter { restaurant ->
            val pickupHoursToday = restaurant.pickupHours.onDay(
                Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            )

            val intent = Intent(this@RestaurantSearchView, RestaurantPageView::class.java).apply {
                putExtra("restaurantId", restaurant.id)
                putExtra("restaurantName", restaurant.name)
                putExtra("restaurantBagPrice", restaurant.bagPrice)
                putExtra("restaurantBagCount", restaurant.bagsAvailable)
                putExtra("restaurantPickupStart", pickupHoursToday.startTime)
                putExtra("restaurantPickupEnd", pickupHoursToday.endTime)
                putExtra("restaurantAddress", restaurant.address)
                putExtra("restaurantBannerImagePath", restaurant.bannerImagePath)
            }
            startActivity(intent)
        }
        restaurantRecyclerView.adapter = restaurantAdapter

        val backArrow = findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener { finish() }

        val filterSpinner = findViewById<Spinner>(R.id.filterSpinner)
        val options = arrayOf(
            "Price: Lowest to Highest",
            "Price: Highest to Lowest",
            "Alphabetical: A-Z",
            "Alphabetical: Z-A"
        )
        val spinnerAdapter = ArrayAdapter(
            this@RestaurantSearchView,
            android.R.layout.simple_spinner_item,
            options
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterSpinner.adapter = spinnerAdapter

        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (::restaurants.isInitialized) {
                    when (position) {
                        0 -> sortRestaurantsByPriceAscending()
                        1 -> sortRestaurantsByPriceDescending()
                        2 -> sortRestaurantsAlphabeticallyAZ()
                        3 -> sortRestaurantsAlphabeticallyZA()
                    }
                } else {
                    Log.w(TAG, "Restaurants not initialized yet!")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
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
        if (::restaurantAdapter.isInitialized) {
            restaurantAdapter.updateRestaurants(newRestaurants)
        } else {
            Log.e(TAG, "Adapter is not initialized!")
        }
    }

    private fun sortRestaurantsByPriceAscending() {
        val sorted = restaurants.sortedBy { it.bagPrice }
        restaurantAdapter.updateRestaurants(sorted)
    }

    private fun sortRestaurantsByPriceDescending() {
        val sorted = restaurants.sortedByDescending { it.bagPrice }
        restaurantAdapter.updateRestaurants(sorted)
    }

    private fun sortRestaurantsAlphabeticallyAZ() {
        val sorted = restaurants.sortedBy { it.name.lowercase(Locale.getDefault()) }
        restaurantAdapter.updateRestaurants(sorted)
    }

    private fun sortRestaurantsAlphabeticallyZA() {
        val sorted = restaurants.sortedByDescending { it.name.lowercase(Locale.getDefault()) }
        restaurantAdapter.updateRestaurants(sorted)
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                location_permission_code
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == location_permission_code) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUserLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userLocation = location
                    val locationTextView: TextView = findViewById(R.id.user_location_text)
                    val geocoder = Geocoder(this, Locale.getDefault())

                    try {
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val userAddress = addresses[0].getAddressLine(0)
                            locationTextView.text = "Your location: $userAddress"
                        } else {
                            locationTextView.text = "Unable to determine your address."
                        }
                    } catch (e: Exception) {
                        locationTextView.text = "Error determining your location: ${e.message}"
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(this, "Unable to fetch location. Try again.", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            requestLocationPermission()
        }
    }

    companion object {
        private const val TAG = "RestaurantSearchView"
    }
}
