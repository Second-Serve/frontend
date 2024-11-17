package com.cs407.secondserve.model

import org.json.JSONObject

class UserRegistrationInfo(
    var accountType: AccountType,
    var email: String,
    var password: String,
    var firstName: String,
    var lastName: String,
    var restaurant: RestaurantRegistrationInfo? = null
) {
    fun toJSONObject() : JSONObject {
        val json = JSONObject()
        json.put("account_type", accountType.toString())
        json.put("email", email)
        json.put("first_name", firstName)
        json.put("last_name", lastName)
        json.put("password", password)

        if (accountType == AccountType.BUSINESS && restaurant != null) {
            json.put("restaurant", restaurant!!.toJSONObject())
        }
        
        return json
    }
}
