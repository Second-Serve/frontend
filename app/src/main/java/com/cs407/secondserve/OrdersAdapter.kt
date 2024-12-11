package com.cs407.secondserve

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class OrdersAdapter(private val orders: List<PreviousOrdersView.Order>) : RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    inner class OrdersViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restaurantNameTextView: TextView = view.findViewById(R.id.restaurantNameTextView)
        val orderIdTextView: TextView = view.findViewById(R.id.orderIdTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrdersViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val currentOrder = orders[position]
        holder.restaurantNameTextView.text = "Restaurant Name: ${currentOrder.restaurantName}"
        holder.orderIdTextView.text = "Order ID: ${currentOrder.orderId}"
    }

    override fun getItemCount(): Int = orders.size
}