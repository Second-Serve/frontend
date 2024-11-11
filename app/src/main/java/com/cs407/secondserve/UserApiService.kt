package com.cs407.secondserve

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class UserApiService(context: Context) {
    private val BASE_URL = "http://<your-backend-ip>:80"
    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)

    fun fetchUsers(bearerToken: String?, onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit) {
        val url = "$BASE_URL/users"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response -> onSuccess(response) },
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
