package com.cs407.secondserve.model

import org.json.JSONObject

class DailyPickupHours(
    val startTime: String,
    val endTime: String
) {
    fun toJSONObject() : JSONObject {
        val json = JSONObject()
        json.put("start_time", startTime)
        json.put("end_time", endTime)
        return json
    }

    companion object {
        val NEVER = DailyPickupHours(
            startTime = "00:00",
            endTime = "00:00"
        )

        val ALWAYS = DailyPickupHours(
            startTime = "00:00",
            endTime = "24:00"
        )

        fun fromJSONObject(json: JSONObject) : DailyPickupHours {
            return DailyPickupHours(
                startTime = json.getString("start_time"),
                endTime = json.getString("end_time")
            )
        }
    }
}
