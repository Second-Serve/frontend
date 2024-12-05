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
            return Restaurant(
                document.id,
                document.getString("name") ?: "Unknown name",
                document.getString("address") ?: "Unknown address",
                document.getGeoPoint("location") ?: GeoPoint(0.0, 0.0),
                WeeklyPickupHours.fromDailyHours(
                    document.getString("pickup_start_time") ?: "0:00",
                    document.getString("pickup_end_time") ?: "0:00"
                ),
                document.getDouble("bag_price") ?: 0.0,
                (document.getLong("bags_available") ?: 0).toInt(),
                (document.getLong("bags_claimed") ?: 0).toInt()
            )
        }
    }
}