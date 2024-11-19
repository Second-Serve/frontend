package com.cs407.secondserve.model

import org.json.JSONObject

class User(
    var id: String,
    var accountType: AccountType,
    var email: String,
    var password: String? = null,
    var bearer: String,
    var isAdmin: Boolean,
    var firstName: String,
    var lastName: String,
    var campusId: Int? = null,
    var restaurant: Restaurant? = null
) {
    companion object {
        fun fromJSONObject(json: JSONObject) : User {
            val user = User(
                id = json.getString("id"),
                accountType = AccountType.fromString(json.getString("account_type")),
                email = json.getString("email"),
                password = json.getString("password"),
                bearer = json.getString("bearer"),
                isAdmin = json.getBoolean("is_admin"),
                firstName = json.getString("first_name"),
                lastName = json.getString("last_name")
            )

            if (!json.isNull("campus_id")) {
                user.campusId = json.getInt("campus_id")
            }

            if (!json.isNull("restaurant")) {
                user.restaurant = Restaurant.fromJSONObject(json.getJSONObject("restaurant"))
            }

            return user
        }
    }
}
