package com.cs407.secondserve.model

import org.json.JSONObject

open class RestaurantRegistrationInfo(
    val name: String,
    val address: String,
    val pickupHours: WeeklyPickupHours,
    val bagPrice: Double,
    val bagsAvailable: Int
) {
    fun toJSONObject() : JSONObject {
        val json = JSONObject()
        json.put("name", name)
        json.put("address", address)
        json.put("pickup_hours", pickupHours.toJSONObject())
        json.put("bag_price", bagPrice)
        json.put("bags_available", bagsAvailable)

        return json
    }
}