package com.cs407.secondserve.model

import org.json.JSONObject

class DailyPickupHours(
    val weekday: Weekday,
    val startTime: String,
    val endTime: String
) {
    fun toJSONObject() : JSONObject {
        val json = JSONObject()
        json.put("weekday", weekday)
        json.put("start_time", startTime)
        json.put("end_time", endTime)
        return json
    }
}
