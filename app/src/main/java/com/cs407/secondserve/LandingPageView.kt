package com.cs407.secondserve

import android.os.Bundle
import android.widget.Button

class LandingPageView : SecondServeView() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeActivity()
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null && !FORCE_LANDING_PAGE) {
            startActivityEmptyIntent(RestaurantSearchView::class.java)
        }
    }

    private fun initializeActivity() {
        setContentView(R.layout.activity_main)

        val userLogInButton: Button = findViewById(R.id.landing_log_in_button)
        val userSignUpButton: Button = findViewById(R.id.landing_sign_up_button)

        userSignUpButton.setOnClickListener { startActivityEmptyIntent(GetStartedView::class.java) }
        userLogInButton.setOnClickListener { startActivityEmptyIntent(LoginView::class.java) }
    }

    companion object {
        private const val FORCE_LANDING_PAGE = true // DEBUG: Ignore saved credentials, show landing page anyways
    }
}