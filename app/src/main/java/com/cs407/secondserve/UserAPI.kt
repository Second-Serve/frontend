package com.cs407.secondserve

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.cs407.secondserve.model.User
import com.cs407.secondserve.model.UserLoginInfo
import com.cs407.secondserve.model.UserRegistrationInfo
import org.json.JSONObject

private const val BASE_URL = "http://10.0.2.2:80"

class UserAPI {
    companion object {
        private lateinit var requestQueue: RequestQueue

        var user: User? = null

        fun init(context: Context) {
            requestQueue = Volley.newRequestQueue(context)
            requestQueue.start()
        }

        private fun makeRequest(
            endpoint: String,
            method: Int,
            onSuccess: (JSONObject) -> Unit,
            onError: ((VolleyError, String) -> Unit)? = null,
            body: JSONObject? = null
        ) {
            val url = "$BASE_URL/$endpoint"

            val jsonObjectRequest = object : JsonObjectRequest(
                method, url, body,
                { response ->
                    val responseJSON = getJSONFromResponse(response)
                    onSuccess(responseJSON)
                },
                { error ->
                    if (onError != null) {
                        val errorMessage = when {
                            error.networkResponse != null -> "Network error: ${error.networkResponse.statusCode}"
                            error.cause != null -> "Error caused by: ${error.cause?.localizedMessage ?: "Unknown cause"}"
                            else -> "Unknown error"
                        }
                        onError(error, errorMessage)
                    }
                }
            ) {
                override fun getHeaders(): Map<String, String> {
                    return mapOf("Authorization" to "Bearer ${user?.bearer}")
                }
            }

            requestQueue.add(jsonObjectRequest)
        }

        private fun getJSONFromResponse(json: JSONObject) : JSONObject {
            return json.getJSONObject("result")
        }

        fun registerAccount(
            registrationInfo: UserRegistrationInfo,
            onSuccess: (User) -> Unit,
            onError: ((VolleyError, String) -> Unit)?
        ) {
            val body = registrationInfo.toJSONObject()
            makeRequest(
                endpoint = "users/",
                method = Request.Method.POST,
                onSuccess = { response ->
                    val user = User.fromJSONObject(response)
                    onSuccess(user)
                },
                onError = onError,
                body = body
            )
        }

        fun login(
            email: String,
            password: String,
            onSuccess: (User) -> Unit,
            onError: ((VolleyError, String) -> Unit)?
        ) {
            val loginInfo = UserLoginInfo(email, password)
            val body = loginInfo.toJSONObject()
            println(body)
            makeRequest(
                endpoint = "users/login",
                method = Request.Method.POST,
                onSuccess = { response ->
                    val user = User.fromJSONObject(response)
                    onSuccess(user)
                },
                onError = onError,
                body = body
            )
        }

        fun fetchUsers(
            onSuccess: (List<User>) -> Unit,
            onError: ((VolleyError, String) -> Unit)?
        ) {
            makeRequest(
                endpoint = "users/",
                method = Request.Method.GET,
                onSuccess = { response ->
                    val usersJSON = response.getJSONArray("users")
                    val users = buildList {
                        for (i in 0..<usersJSON.length()) {
                            val userJSON = usersJSON.getJSONObject(i)
                            add(User.fromJSONObject(userJSON))
                        }
                    }
                    onSuccess(users)
                },
                onError = onError
            )
        }

        fun cancelAllRequests() {
            requestQueue.cancelAll("UserApiService")
        }
    }
}
