package com.cs407.secondserve.model

import org.json.JSONObject

class UserRegistrationInfo(
    val userInfo: UserInfo,
    val password: String
) {
    fun toJSONObject() : JSONObject {
        val json = JSONObject()
        json.put("account_type", userInfo.accountType.toString())
        json.put("email", userInfo.email)
        json.put("first_name", userInfo.firstName)
        json.put("last_name", userInfo.lastName)
        json.put("password", password)

        if (userInfo.accountType == AccountType.BUSINESS) {
            json.put("business_name", userInfo.businessName)
            json.put("business_address", userInfo.businessAddress)
            json.put("business_pickup_hours", userInfo.businessPickupHours?.toJSONObject())
        }
        
        return json
    }
}
