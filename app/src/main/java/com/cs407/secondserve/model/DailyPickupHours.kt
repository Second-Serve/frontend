package com.cs407.secondserve.model

class DailyPickupHours(
    val startTime: String,
    val endTime: String
) {
    companion object {
        val NEVER = DailyPickupHours(
            startTime = "00:00",
            endTime = "00:00"
        )

        val ALWAYS = DailyPickupHours(
            startTime = "00:00",
            endTime = "24:00"
        )
    }
}
