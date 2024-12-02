package com.cs407.secondserve.service

import com.cs407.secondserve.model.Restaurant
import com.cs407.secondserve.model.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RestaurantService {
    companion object {
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
            if (bagPrice != null && bagPrice < 0) {
                throw IllegalArgumentException("Bag price must not be negative.")
            }

            if (bagsAvailable < 0) {
                throw IllegalArgumentException("Number of bags available must not be negative.")
            }

            val db = Firebase.firestore

            val userInfo = hashMapOf(
                "owner" to db.document("users/${forUser.id}"),
                "name" to name,
                "address" to address,
                "pickup_start_time" to pickupStartTime,
                "pickup_end_time" to pickupEndTime,
                "bag_price" to bagPrice,
                "bags_available" to bagsAvailable,
                "bags_claimed" to 0
            )

            db.collection("restaurants")
                .add(userInfo)
                .addOnSuccessListener {
                    onSuccess?.invoke()
                }
                .addOnFailureListener { exception ->
                    onFailure?.invoke(exception)
                }
        }

        fun fetchAll(
            onSuccess: ((List<Restaurant>) -> Unit)? = null,
            onFailure: ((Exception) -> Unit)? = null
        ) {
            val db = Firebase.firestore
            val future = db.collection("restaurants").get()

            future.addOnSuccessListener { result ->
                // Put all restaurants into a list
                val restaurants = mutableListOf<Restaurant>()
                var restaurantsNotLoaded = 0
                for (restaurantDocument in result.documents) {
                    try {
                        restaurants += Restaurant.fromFetchedDocument(restaurantDocument)
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                        restaurantsNotLoaded++
                    }
                }

                // If we failed to load any restaurants, print that to the console
                if (restaurantsNotLoaded > 0) {
                    println("Couldn't load $restaurantsNotLoaded restaurant(s).")
                }

                onSuccess?.invoke(restaurants)
            }

            future.addOnFailureListener { exception ->
                onFailure?.invoke(exception)
            }
        }
    }
}