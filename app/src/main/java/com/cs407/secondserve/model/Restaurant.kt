package com.cs407.secondserve.model

import org.json.JSONObject

class Restaurant(
    val id: String,
    val name: String,
    val address: String,
    val pickupHours: WeeklyPickupHours,
    val menuItems: List<String>
) {
    fun toJSONObject() : JSONObject {
        val json = JSONObject()
        json.put("id", id)
        json.put("name", name)
        json.put("address", address)
        json.put("pickup_hours", pickupHours.toJSONObject())
        json.put("menu_items", menuItems)

        return json
    }

    companion object {
        fun fromJSONObject(json: JSONObject) : Restaurant {
            return Restaurant(
                id = json.getString("id"),
                name = json.getString("name"),
                address = json.getString("address"),
                pickupHours = WeeklyPickupHours.fromJSONObject(json.getJSONObject("pickup_hours")),
                menuItems = emptyList() // TODO
            )
        }
    }
}