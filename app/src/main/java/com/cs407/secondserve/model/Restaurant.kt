package com.cs407.secondserve.model

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint

class Restaurant(
    val id: String,
    val name: String,
    val address: String,
    val location: GeoPoint,
    val pickupHours: WeeklyPickupHours,
    val bagPrice: Double? = null,
    val bagsAvailable: Int,
    val bagsClaimed: Int,
    var bannerImagePath: String? = null
) {
    companion object {
        fun fromFetchedDocument(document: DocumentSnapshot) : Restaurant {
            println(document.getString("name")!!)
            return Restaurant(
                document.id,
                document.getString("name")!!,
                document.getString("address")!!,
                document.getGeoPoint("location")!!,
                WeeklyPickupHours.fromDailyHours(
                    document.getString("pickup_start_time")!!,
                    document.getString("pickup_end_time")!!
                ),
                document.getDouble("bag_price"),
                document.getLong("bags_available")!!.toInt(),
                (document.getLong("bags_claimed") ?: 0).toInt()
            )
        }
    }
}