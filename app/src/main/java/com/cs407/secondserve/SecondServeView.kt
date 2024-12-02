package com.cs407.secondserve

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cs407.secondserve.service.AccountService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

open class SecondServeView : AppCompatActivity() {
    fun <T : Activity> startActivityEmptyIntent(clazz: Class<T>) {
        val intent = Intent(this, clazz)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AccountService.auth = Firebase.auth
    }

    companion object {
        const val TAG = "SECOND SERVE"
        const val TAG_ERROR = "SECOND SERVE ERROR"
    }
}