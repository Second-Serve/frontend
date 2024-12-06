package com.cs407.secondserve

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs407.secondserve.service.AccountService
import com.cs407.secondserve.service.LocationService
import com.cs407.secondserve.service.LocationService.Companion
import com.cs407.secondserve.util.Debug
import com.google.firebase.database.ktx.database
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class LandingPageView : SecondServeView() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        runBlocking{
//            suspend fun validateAddress(address: String): Boolean {
//                if (address.isBlank()) {
//                    Log.d(LocationService.TAG, "Address is empty or null")
//                    return false
//                }
//
//                try {
//                    val data = hashMapOf("address" to address)
//                    val result = Firebase.functions
//                        .getHttpsCallable("isAddressValid")
//                        .call(data)
//                        .await()
//                    val resultMap = result.getData() as? Map<*, *>
//                    val isValid = resultMap?.get("isValid") as? Boolean ?: false
//
//                    Log.d(LocationService.TAG, "Address validation result for '$address': $isValid")
//                    return isValid
//                } catch (e: Exception) {
//                    Log.e(LocationService.TAG, "Error validating address: ${e.message}", e)
//                    return false
//                }
//            }

        if (Debug.USE_FIREBASE_EMULATOR) {
//            Firebase.auth.useEmulator("10.0.2.2", 9099)
            Firebase.database.useEmulator("10.0.2.2", 9000)
            Firebase.functions.useEmulator("10.0.2.2", 5001)
        }

        val prefs = getSharedPreferences("com.cs407.secondserve", Context.MODE_PRIVATE)
        val savedEmail = prefs.getString(getString(R.string.saved_email_key), null)
        val savedPassword = prefs.getString(getString(R.string.saved_password_key), null)

        if (savedEmail != null && savedPassword != null && !Debug.FORCE_LANDING_PAGE) {
            AccountService.signIn(
                savedEmail,
                savedPassword,
                onSuccess = { _, _ -> loadRestaurantSearch() },
                onFailure = { _ -> loadRecyclerView() }
            )
        } else {
            loadRecyclerView()
        }
    }

    private fun loadRecyclerView() {
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val items = listOf("Log In", "Sign Up")
        recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.button, parent, false)
                return object : RecyclerView.ViewHolder(view) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val button = holder.itemView.findViewById<Button>(R.id.item_button)
                button.text = items[position]
                button.setOnClickListener {
                    when (items[position]) {
                        "Log In" -> loadLogIn()
                        "Sign Up" -> loadSignUp()
                    }
                }
            }

            override fun getItemCount(): Int = items.size
        }
    }

    private fun loadSignUp() {
        startActivity(Intent(this, GetStartedView::class.java))
    }

    private fun loadLogIn() {
        startActivity(Intent(this, LoginView::class.java))
    }

    private fun loadRestaurantSearch() {
        startActivity(Intent(this, RestaurantSearchView::class.java))
    }
}