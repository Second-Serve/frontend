package com.cs407.secondserve

import android.content.Intent
import com.cs407.secondserve.R
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar


class PreviousOrdersView : SecondServeView(){
    private lateinit var toolbar: MaterialToolbar
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var ordersRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.previous_orders)

        toolbar = findViewById(R.id.topAppBar)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, RestaurantSearchView::class.java)
            startActivity(intent)
        }

        ordersRecyclerView.layoutManager = LinearLayoutManager(this)
        val orders = getPreviousOrders()

        if (orders.isEmpty()) {
            emptyStateLayout.visibility = View.VISIBLE
            ordersRecyclerView.visibility = View.GONE
        } else {
            emptyStateLayout.visibility = View.GONE
            ordersRecyclerView.visibility = View.VISIBLE

            val adapter = OrdersAdapter(orders)
            ordersRecyclerView.adapter = adapter
        }
    }


    private fun getPreviousOrders(): List<String> {
        // Replace with actual data fetching logic
        return listOf() // Example: no orders (to show empty state)

    }
}