package com.cs407.secondserve.model

class WeeklyPickupHours(
    val sunday: DailyPickupHours,
    val monday: DailyPickupHours,
    val tuesday: DailyPickupHours,
    val wednesday: DailyPickupHours,
    val thursday: DailyPickupHours,
    val friday: DailyPickupHours,
    val saturday: DailyPickupHours
) {
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

        fun fromDailyHours(dailyPickupHours: DailyPickupHours) : WeeklyPickupHours {
            return WeeklyPickupHours(
                dailyPickupHours,
                dailyPickupHours,
                dailyPickupHours,
                dailyPickupHours,
                dailyPickupHours,
                dailyPickupHours,
                dailyPickupHours
            )
        }

        fun fromDailyHours(dailyStartTime: String, dailyEndTime: String) : WeeklyPickupHours {
            val dailyPickupHours = DailyPickupHours(dailyStartTime, dailyEndTime)
            return fromDailyHours(dailyPickupHours)
        }
    }
}
