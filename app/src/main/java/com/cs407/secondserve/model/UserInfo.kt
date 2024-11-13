package com.cs407.secondserve.model

data class UserInfo(
    val accountType: AccountType,
    val email: String,
    val isAdmin: Boolean,
    val firstName: String,
    val lastName: String,
    val businessName: String,
    val businessAddress: String,
    val businessPickupHours: WeeklyPickupHours
)
