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

    companion object {
        // Useful for placeholders
        val ALWAYS = WeeklyPickupHours(
            sunday = DailyPickupHours("00:00", "24:00"),
            monday = DailyPickupHours("00:00", "24:00"),
            tuesday = DailyPickupHours("00:00", "24:00"),
            wednesday = DailyPickupHours("00:00", "24:00"),
            thursday = DailyPickupHours("00:00", "24:00"),
            friday = DailyPickupHours("00:00", "24:00"),
            saturday = DailyPickupHours("00:00", "24:00")
        )

        fun fromJSONObject(json: JSONObject) : WeeklyPickupHours {
            return WeeklyPickupHours(
                sunday = DailyPickupHours.fromJSONObject(json.getJSONObject("sunday")),
                monday = DailyPickupHours.fromJSONObject(json.getJSONObject("monday")),
                tuesday = DailyPickupHours.fromJSONObject(json.getJSONObject("tuesday")),
                wednesday = DailyPickupHours.fromJSONObject(json.getJSONObject("wednesday")),
                thursday = DailyPickupHours.fromJSONObject(json.getJSONObject("thursday")),
                friday = DailyPickupHours.fromJSONObject(json.getJSONObject("friday")),
                saturday = DailyPickupHours.fromJSONObject(json.getJSONObject("saturday"))
            )
        }
    }
}
