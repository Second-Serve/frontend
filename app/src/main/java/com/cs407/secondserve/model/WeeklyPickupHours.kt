package com.cs407.secondserve.model

import org.json.JSONObject

class WeeklyPickupHours(
    val sunday: DailyPickupHours,
    val monday: DailyPickupHours,
    val tuesday: DailyPickupHours,
    val wednesday: DailyPickupHours,
    val thursday: DailyPickupHours,
    val friday: DailyPickupHours,
    val saturday: DailyPickupHours
) {
    fun toJSONObject() : JSONObject {
        val json = JSONObject()
        json.put("sunday", sunday.toJSONObject())
        json.put("monday", monday.toJSONObject())
        json.put("tuesday", tuesday.toJSONObject())
        json.put("wednesday", wednesday.toJSONObject())
        json.put("thursday", thursday.toJSONObject())
        json.put("friday", friday.toJSONObject())
        json.put("saturday", saturday.toJSONObject())
        return json
    }
}
