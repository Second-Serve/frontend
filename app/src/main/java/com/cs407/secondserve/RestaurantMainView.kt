package com.cs407.secondserve

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RestaurantMainView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_restaurant_sign_up)

        val signUpButton = findViewById<Button>(R.id.restaurant_sign_up_button)

        signUpButton.setOnClickListener {
            setContentView(R.layout.restaurant_main_page)
        }
    }
}
