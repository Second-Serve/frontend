package com.cs407.secondserve

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_get_started)


        val customerButton: Button = findViewById(R.id.button)

        customerButton.setOnClickListener {
            val intent = Intent(this, SignUpUser::class.java)
            startActivity(intent)
        }
    }


}
