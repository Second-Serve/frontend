package com.cs407.secondserve

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs407.secondserve.service.AccountService
import com.cs407.secondserve.util.Debug
import com.google.firebase.database.ktx.database
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class LandingPageView : SecondServeView() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Debug.USE_FIREBASE_EMULATOR) {
//            Firebase.auth.useEmulator("10.0.2.2", 9099)
            Firebase.database.useEmulator("10.0.2.2", 9000)
            Firebase.functions.useEmulator("10.0.2.2", 5001)
        }

//        runBlocking {
//            LocationService.validateAddress("3 N Randall Ave")
//        }

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