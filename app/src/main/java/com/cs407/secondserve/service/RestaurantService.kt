package com.cs407.secondserve.service

import com.cs407.secondserve.model.Restaurant
import com.cs407.secondserve.model.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.util.Log

class RestaurantService {
    companion object {
        private const val TAG = "RestaurantService"

        /**
         * Creates a new restaurant document in Firestore.
         */
        fun create(
            forUser: User,
            name: String,
            address: String,
            pickupStartTime: String,
            pickupEndTime: String,
            bagPrice: Double? = null,
            bagsAvailable: Int = 0,
            onSuccess: (() -> Unit)? = null,
            onFailure: ((Exception) -> Unit)? = null
        ) {
            try {
                // Validate input
                require(bagPrice == null || bagPrice >= 0) { "Bag price must not be negative." }
                require(bagsAvailable >= 0) { "Number of bags available must not be negative." }

                val db = Firebase.firestore
                val restaurantInfo = hashMapOf(
                    "owner" to db.document("users/${forUser.id}"),
                    "name" to name,
                    "address" to address,
                    "pickup_start_time" to pickupStartTime,
                    "pickup_end_time" to pickupEndTime,
                    "bag_price" to bagPrice,
                    "bags_available" to bagsAvailable,
                    "bags_claimed" to 0
                )

                // Add to Firestore
                db.collection("restaurants")
                    .add(restaurantInfo)
                    .addOnSuccessListener {
                        Log.d(TAG, "Restaurant created successfully.")
                        onSuccess?.invoke()
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Error creating restaurant: ${exception.message}", exception)
                        onFailure?.invoke(exception)
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Invalid restaurant data: ${e.message}", e)
                onFailure?.invoke(e)
            }
        }

        /**
         * Fetches a single restaurant by ID.
         */
        fun fetch(
            id: String,
            onSuccess: ((Restaurant) -> Unit)? = null,
            onFailure: ((Exception) -> Unit)? = null
        ) {
            val db = Firebase.firestore
            db.collection("restaurants")
                .document(id)
                .get()
                .addOnSuccessListener { result ->
                    try {
                        val restaurant = Restaurant.fromFetchedDocument(result)
                        onSuccess?.invoke(restaurant)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing restaurant document: ${e.message}", e)
                        onFailure?.invoke(e)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error fetching restaurant: ${exception.message}", exception)
                    onFailure?.invoke(exception)
                }
        }

        /**
         * Fetches all restaurants from Firestore.
         */
        fun fetchAll(
            onSuccess: ((List<Restaurant>) -> Unit)? = null,
            onFailure: ((Exception) -> Unit)? = null
        ) {
            val db = Firebase.firestore
            db.collection("restaurants")
                .get()
                .addOnSuccessListener { result ->
                    val restaurants = mutableListOf<Restaurant>()
                    var failedCount = 0

                    for (document in result.documents) {
                        try {
                            restaurants.add(Restaurant.fromFetchedDocument(document))
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing restaurant document: ${e.message}", e)
                            failedCount++
                        }
                    }

                    if (failedCount > 0) {
                        Log.w(TAG, "$failedCount restaurant(s) failed to load.")
                    }
                    onSuccess?.invoke(restaurants)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error fetching restaurants: ${exception.message}", exception)
                    onFailure?.invoke(exception)
                }
        }

        /**
         * Fetches the first restaurant for a given user by their ID.
         */
        fun fetchByUserId(
            userId: String,
            onSuccess: ((Restaurant?) -> Unit)? = null,
            onFailure: ((Exception) -> Unit)? = null
        ) {
            val db = Firebase.firestore
            db.collection("restaurants")
                .whereEqualTo("owner.id", userId)
                .get()
                .addOnSuccessListener { result ->
                    try {
                        if (result.documents.isEmpty()) {
                            Log.d(TAG, "No restaurants found for user ID: $userId")
                            onSuccess?.invoke(null)
                        } else {
                            val restaurant = Restaurant.fromFetchedDocument(result.documents[0])
                            onSuccess?.invoke(restaurant)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing restaurant document: ${e.message}", e)
                        onFailure?.invoke(e)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error fetching restaurant by user ID: ${exception.message}", exception)
                    onFailure?.invoke(exception)
                }
        }
    }
}