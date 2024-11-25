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

    fun onDay(weekday: Weekday) : DailyPickupHours {
        return when (weekday) {
            Weekday.SUNDAY -> sunday
            Weekday.MONDAY -> monday
            Weekday.TUESDAY -> tuesday
            Weekday.WEDNESDAY -> wednesday
            Weekday.THURSDAY -> thursday
            Weekday.FRIDAY -> friday
            Weekday.SATURDAY -> saturday
        }
    }

    @OptIn(kotlin.ExperimentalStdlibApi::class)
    fun onDay(weekday: Int) : DailyPickupHours {
        return onDay(Weekday.entries[weekday])
    }

    companion object {
        // Useful for placeholders
        val ALWAYS = WeeklyPickupHours(
            sunday = DailyPickupHours.ALWAYS,
            monday = DailyPickupHours.ALWAYS,
            tuesday = DailyPickupHours.ALWAYS,
            wednesday = DailyPickupHours.ALWAYS,
            thursday = DailyPickupHours.ALWAYS,
            friday = DailyPickupHours.ALWAYS,
            saturday = DailyPickupHours.ALWAYS
        )

        val NEVER = WeeklyPickupHours(
            sunday = DailyPickupHours.NEVER,
            monday = DailyPickupHours.NEVER,
            tuesday = DailyPickupHours.NEVER,
            wednesday = DailyPickupHours.NEVER,
            thursday = DailyPickupHours.NEVER,
            friday = DailyPickupHours.NEVER,
            saturday = DailyPickupHours.NEVER
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
