package com.cs407.secondserve.model

data class WeeklyPickupHours(
    val sunday: DailyPickupHours,
    val monday: DailyPickupHours,
    val tuesday: DailyPickupHours,
    val wednesday: DailyPickupHours,
    val thursday: DailyPickupHours,
    val friday: DailyPickupHours,
    val saturday: DailyPickupHours
)
