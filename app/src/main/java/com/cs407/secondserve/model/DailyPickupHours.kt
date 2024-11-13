package com.cs407.secondserve.model

data class DailyPickupHours(
    val weekday: Weekday,
    val startTime: String,
    val endTime: String
)
