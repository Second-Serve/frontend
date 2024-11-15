package com.cs407.secondserve.model

data class UserInfo(
    val accountType: AccountType,
    val email: String,
    val firstName: String,
    val lastName: String,
    val businessName: String? = null,
    val businessAddress: String? = null,
    val businessPickupHours: WeeklyPickupHours? = null
)
