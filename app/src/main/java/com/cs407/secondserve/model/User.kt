package com.cs407.secondserve.model

import org.json.JSONObject

class User(
    info: UserInfo,
    id: String,
    bearer: String?,
    isAdmin: Boolean = false,
) {
    companion object {
        fun fromJSONObject(json: JSONObject) : User {
            return User(
                UserInfo(
                    accountType = AccountType.fromString(json.getString("account_type")),
                    email = json.getString("email"),
                    firstName = json.getString("first_name"),
                    lastName = json.getString("last_name")
                ),
                id = json.getString("id"),
                bearer = json.getString("bearer"),
                isAdmin = json.getBoolean("is_admin")
            )
        }
    }
}
