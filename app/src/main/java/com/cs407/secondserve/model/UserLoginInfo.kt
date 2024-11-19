package com.cs407.secondserve.model

import org.json.JSONObject

class UserLoginInfo(
    val email: String,
    val password: String
) {
    fun toJSONObject() : JSONObject {
        val json = JSONObject()
        json.put("email", email)
        json.put("password", password)

        return json
    }
}
