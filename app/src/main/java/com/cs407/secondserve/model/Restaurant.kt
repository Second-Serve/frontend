package com.cs407.secondserve.model

import org.json.JSONObject

class Restaurant(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val pickupHours: WeeklyPickupHours,
    val bagPrice: Double,
    val bagsAvailable: Int,
    val bagsClaimed: Int,
    var bannerImagePath: String? = null
) {
    fun toJSONObject() : JSONObject {
        val json = JSONObject()
        json.put("id", id)
        json.put("name", name)
        json.put("latitude", latitude)
        json.put("longitude", longitude)
        json.put("address", address)
        json.put("pickup_hours", pickupHours.toJSONObject())
        json.put("bag_price", bagPrice)
        json.put("bags_available", bagsAvailable)
        json.put("bags_claimed", bagsClaimed)
        json.put("banner_image_path", bannerImagePath)

        return json
    }

    companion object {
        fun fromJSONObject(json: JSONObject) : Restaurant {
            val restaurant = Restaurant(
                id = json.getString("id"),
                name = json.getString("name"),
                latitude = json.getDouble("latitude"),
                longitude = json.getDouble("longitude"),
                address = json.getString("address"),
                pickupHours = WeeklyPickupHours.fromJSONObject(json.getJSONObject("pickup_hours")),
                bagPrice = json.getDouble("bag_price"),
                bagsAvailable = json.getInt("bags_available"),
                bagsClaimed = json.getInt("bags_claimed")
            )

            if (!json.isNull("banner_image_path")) {
                restaurant.bannerImagePath = json.getString("banner_image_path")
            }

            return restaurant
        }
    }
}