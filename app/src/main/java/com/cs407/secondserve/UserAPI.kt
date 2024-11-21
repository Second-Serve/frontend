package com.cs407.secondserve

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.cs407.secondserve.model.Restaurant
import com.cs407.secondserve.model.User
import com.cs407.secondserve.model.UserLoginInfo
import com.cs407.secondserve.model.UserRegistrationInfo
import com.cs407.secondserve.util.APIImageLoader
import com.cs407.secondserve.util.AppImageCache
import org.json.JSONObject

private const val BASE_URL = "http://10.0.2.2:80"

class UserAPI {
    companion object {
        lateinit var requestQueue: RequestQueue

        lateinit var imageLoader: APIImageLoader

        var user: User? = null

        fun init(context: Context) {
            requestQueue = Volley.newRequestQueue(context)
            requestQueue.start()

            imageLoader = APIImageLoader(
                requestQueue,
                AppImageCache(32),
                BASE_URL
            )
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

        fun saveUser(context: Context) {
            val sharedPreferences = context.getSharedPreferences("com.cs407.secondserve", Context.MODE_PRIVATE)
            with (sharedPreferences.edit()) {
                putString("com.cs407.secondserve.email", user?.email)
                putString("com.cs407.secondserve.password", user?.password)
                apply()
            }
        }

        fun registerAccount(
            registrationInfo: UserRegistrationInfo,
            onSuccess: (User) -> Unit,
            onError: ((VolleyError, String) -> Unit)? = null
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
            onError: ((VolleyError, String) -> Unit)? = null
        ) {
            val loginInfo = UserLoginInfo(email, password)
            val body = loginInfo.toJSONObject()
            makeRequest(
                endpoint = "users/login",
                method = Request.Method.POST,
                onSuccess = { response ->
                    val user = User.fromJSONObject(response)
                    this.user = user
                    onSuccess(user)
                },
                onError = onError,
                body = body
            )
        }

        fun fetchUsers(
            onSuccess: (List<User>) -> Unit,
            onError: ((VolleyError, String) -> Unit)? = null
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

        fun fetchRestaurants(
            onSuccess: (List<Restaurant>) -> Unit,
            onError: ((VolleyError, String) -> Unit)? = null
        ) {
            makeRequest(
                endpoint = "restaurants/",
                method = Request.Method.GET,
                onSuccess = { response ->
                    val restaurantsJSON = response.getJSONArray("restaurants")
                    val restaurants = buildList {
                        for (i in 0..<restaurantsJSON.length()) {
                            val restaurantJSON = restaurantsJSON.getJSONObject(i)
                            add(Restaurant.fromJSONObject(restaurantJSON))
                        }
                    }
                    onSuccess(restaurants)
                },
                onError = onError
            )
        }

        fun cancelAllRequests() {
            requestQueue.cancelAll("UserApiService")
        }
    }
}
