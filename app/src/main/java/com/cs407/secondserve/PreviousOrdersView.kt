package com.cs407.secondserve

import com.cs407.secondserve.R
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar


class PreviousOrdersView : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.previous_orders)
        val topAppBar: MaterialToolbar = findViewById(R.id.topAppBar)

        setSupportActionBar(topAppBar)
        topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}