package com.cs407.secondserve

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GetStarted : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_get_started)

        val customerButton: Button = findViewById(R.id.customer_button)
        val businessButton: Button = findViewById(R.id.business_button)

        customerButton.setOnClickListener {
            val intent = Intent(this, SignUpUser::class.java)
            startActivity(intent)
        }

        businessButton.setOnClickListener {
            val intent = Intent(this, RestaurantSignUp::class.java)
            startActivity(intent)
        }
    }
}