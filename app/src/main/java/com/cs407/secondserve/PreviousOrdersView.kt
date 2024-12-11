package com.cs407.secondserve

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreviousOrdersView : SecondServeView() {

    data class Order(
        val restaurantName: String,
        val orderId: String
    )

    private lateinit var toolbar: MaterialToolbar
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var adapter: OrdersAdapter
    private val orders = mutableListOf<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.previous_orders)

        toolbar = findViewById(R.id.topAppBar)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        ordersRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = OrdersAdapter(orders)
        ordersRecyclerView.adapter = adapter

        fetchPreviousOrders()
    }

    private fun fetchPreviousOrders() {
        Firebase.functions.getHttpsCallable("getOrders")
            .call()
            .addOnSuccessListener { result ->
                try {
                    Log.d("PreviousOrdersView", "Raw Firebase result: ${result.getData()}")

                    val gson = Gson()
                    val orderListType = object : TypeToken<List<Order>>() {}.type
                    val fetchedOrders: List<Order> = gson.fromJson(result.getData().toString(), orderListType)

                    Log.d("PreviousOrdersView", "Parsed orders: $fetchedOrders")

                    orders.clear()
                    orders.addAll(fetchedOrders)
                    adapter.notifyDataSetChanged()

                    if (orders.isEmpty()) {
                        emptyStateLayout.visibility = View.VISIBLE
                        ordersRecyclerView.visibility = View.GONE
                    } else {
                        emptyStateLayout.visibility = View.GONE
                        ordersRecyclerView.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    Log.e("PreviousOrdersView", "Error parsing orders: ${e.message}")
                    showErrorState()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("PreviousOrdersView", "Failed to fetch orders: ${exception.message}")
                showErrorState()
            }
    }

    private fun showErrorState() {
        emptyStateLayout.visibility = View.VISIBLE
        ordersRecyclerView.visibility = View.GONE
    }
}