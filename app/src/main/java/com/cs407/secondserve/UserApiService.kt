package com.cs407.secondserve

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.cs407.secondserve.model.UserRegistrationInfo
import org.json.JSONObject

private const val BASE_URL = "http://10.0.2.2:80"

class UserAPI(context: Context) {
    private var requestQueue: RequestQueue = Volley.newRequestQueue(context)

    val bearerToken: String? = null

    init {
        requestQueue.start()
    }

    private fun makeRequest(endpoint: String, method: Int, onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit) {
        val url = "$BASE_URL/$endpoint"

        val jsonObjectRequest = object : JsonObjectRequest(
            method, url, null,
            { response ->
                onSuccess(response)
            },
            { error ->
                val errorMessage = when {
                    error.networkResponse != null -> "Network error: ${error.networkResponse.statusCode}"
                    error.cause != null -> "Error caused by: ${error.cause?.localizedMessage ?: "Unknown cause"}"
                    else -> "Unknown error"
                }
                onError(errorMessage)
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                bearerToken?.let {
                    headers["Authorization"] = "Bearer $bearerToken"
                }
                return headers
            }
        }

        requestQueue.add(jsonObjectRequest)
    }

    fun registerAccount(registrationInfo: UserRegistrationInfo) {
        makeRequest("users/", Request.Method.POST,
            { response: JSONObject ->
                println(response)
            },
            { errorMessage -> throw Exception(errorMessage) }
        )
    }

    fun fetchUsers() {
        makeRequest("users/", Request.Method.GET,
            { response: JSONObject ->
                println(response)
            },
            { errorMessage -> throw Exception(errorMessage) }
        )
    }

    fun createAccount(userInfo: JSONObject, onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit) {
        val url = "$BASE_URL/users"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, userInfo,
            { response -> onSuccess(response) },
            { error ->
                val errorMessage = when {
                    error.networkResponse != null -> "Network error: ${error.networkResponse.statusCode}"
                    error.cause != null -> "Error caused by: ${error.cause?.localizedMessage ?: "Unknown cause"}"
                    else -> "Unknown error"
                }
                onError(errorMessage)
            }
        )


        requestQueue.add(jsonObjectRequest)
    }

    fun cancelAllRequests() {
        requestQueue.cancelAll("UserApiService")
    }
}
