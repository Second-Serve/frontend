package com.cs407.secondserve

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs407.secondserve.GetStarted
import com.cs407.secondserve.LoginActivity
import com.cs407.secondserve.R
import com.cs407.secondserve.RestaurantSearch
import com.cs407.secondserve.UserAPI

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        UserAPI.init(this)

        val prefs = getSharedPreferences("com.cs407.secondserve", Context.MODE_PRIVATE)
        val savedEmail = prefs.getString(getString(R.string.saved_email_key), null)
        val savedPassword = prefs.getString(getString(R.string.saved_password_key), null)

        if (savedEmail != null && savedPassword != null && !FORCE_LANDING_PAGE) {
            UserAPI.login(
                savedEmail,
                savedPassword,
                onSuccess = { loadRestaurantSearch() },
                onError = { _, _ -> loadRecyclerView() }
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
        startActivity(Intent(this, GetStarted::class.java))
    }

    private fun loadLogIn() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun loadRestaurantSearch() {
        startActivity(Intent(this, RestaurantSearch::class.java))
    }

    companion object {
        private const val FORCE_LANDING_PAGE = true
    }
}