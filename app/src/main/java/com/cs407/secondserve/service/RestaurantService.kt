package com.cs407.secondserve.service

import com.cs407.secondserve.model.Restaurant
import com.cs407.secondserve.model.RestaurantDashboardInformation
import com.cs407.secondserve.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
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

        fun fetch(
            id: String,
            onSuccess: ((Restaurant) -> Unit)? = null,
            onFailure: ((Exception) -> Unit)? = null
        ) {
            val db = Firebase.firestore
            val future = db.collection("restaurants")
                .document(id)
                .get()

            future.addOnSuccessListener { result ->
                val restaurant = Restaurant.fromFetchedDocument(result)
                onSuccess?.invoke(restaurant)
            }
            future.addOnFailureListener { exception ->
                onFailure?.invoke(exception)
            }
        }

        fun fetch(
            onSuccess: ((Restaurant) -> Unit)? = null,
            onFailure: ((Exception) -> Unit)? = null
        ) {
            val currentUserId = Firebase.auth.currentUser!!.uid
            fetchByUserId(currentUserId, onSuccess, onFailure)
        }

        fun fetchAll(
            onSuccess: ((List<Restaurant>) -> Unit)? = null,
            onFailure: ((Exception) -> Unit)? = null,
            includeHidden: Boolean = false
        ) {
            val db = Firebase.firestore
            val future = db.collection("restaurants").get()

            future.addOnSuccessListener { result ->
                // Put all restaurants into a list
                val restaurants = mutableListOf<Restaurant>()
                var restaurantsNotLoaded = 0
                for (restaurantDocument in result.documents) {
                    try {
                        val restaurant = Restaurant.fromFetchedDocument(restaurantDocument)

                        if (!includeHidden){
                            if (restaurant.bagsAvailable - restaurant.bagsClaimed <= 0) {
                                continue
                            }

                            if (restaurant.bagPrice == null || restaurant.bagPrice <= 0) {
                                continue
                            }
                        }

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

        fun fetchByUserId(
            userId: String,
            onSuccess: ((Restaurant) -> Unit)? = null,
            onFailure: ((Exception) -> Unit)? = null
        ) {
            val db = Firebase.firestore
            val userDocument = db.collection("users").document(userId)
            val future = db.collection("restaurants")
                .whereEqualTo("owner", userDocument)
                .get()

            future.addOnSuccessListener { result ->
                try {
                    val restaurant = Restaurant.fromFetchedDocument(result.documents[0])
                    onSuccess?.invoke(restaurant)
                } catch (e: Exception) {
                    onFailure?.invoke(e)
                }
            }
            future.addOnFailureListener { exception ->
                onFailure?.invoke(exception)
            }
        }

        fun updateRestaurantInformation(
            name: String? = null,
            address: String? = null,
            bagPrice: Double? = null,
            bagsAvailable: Int? = null,
            onSuccess: (() -> Unit)? = null,
            onFailure: ((String) -> Unit)? = null,
            onException: ((Exception) -> Unit)? = null
        ) {
            val data = hashMapOf<String, Any>()

            if (name != null) {
                data["name"] = name
            }

            if (address != null) {
                data["address"] = address
            }

            if (bagPrice != null) {
                data["bag_price"] = bagPrice
            }

            if (bagsAvailable != null) {
                data["bags_available"] = bagsAvailable
            }

            Firebase.functions.getHttpsCallable("updateRestaurantInformation")
                .call(data)
                .addOnSuccessListener { result ->
                    val resultMap = result.getData() as? Map<*, *>
                    val success = resultMap?.get("success") as? Boolean

                    if (success == true) {
                        onSuccess?.invoke()
                    } else {
                        val message = resultMap?.get("reason") as? String
                        onFailure?.invoke(message ?: "Unknown error")
                    }
                }
                .addOnFailureListener { exception ->
                    onException?.invoke(exception)
                }

        }

        fun getRestaurantDashboardInformation(
            onSuccess: ((RestaurantDashboardInformation) -> Unit)? = null,
            onFailure: ((Exception) -> Unit)? = null
        ) {
            Firebase.functions.getHttpsCallable("getRestaurantDashboardInformation")
                .call()
                .addOnSuccessListener {
                    val result = it.getData() as? Map<*, *>
                    result?.let {
                        // .toDouble() is a stupid hack only necessary because of how JS serializes numbers
                        val dashboardInfo = RestaurantDashboardInformation(
                            result["ordersLast24Hours"] as Int,
                            (result["earningsLast24Hours"] as? Number)?.toDouble() ?: 0.0,
                            result["ordersAllTime"] as Int,
                            (result["earningsAllTime"] as? Number)?.toDouble() ?: 0.0
                        )
                        onSuccess?.invoke(dashboardInfo)
                    }
                }
                .addOnFailureListener {
                    onFailure?.invoke(it)
                }
        }
    }
}