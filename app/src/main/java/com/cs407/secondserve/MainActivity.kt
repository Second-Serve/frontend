package com.cs407.secondserve

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val signUpButton: Button = findViewById(R.id.button2)

        signUpButton.setOnClickListener {
            val intent = Intent(this, GetStarted::class.java)
            startActivity(intent)
        }
    }
}
