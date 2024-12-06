package com.cs407.secondserve

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cs407.secondserve.model.Cart
import com.cs407.secondserve.model.CartItem

class CartAdapter : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var cartItems: List<CartItem> = Cart.getItems()

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.item_name_text)
        val itemPrice: TextView = itemView.findViewById(R.id.item_price_text)
        val itemQuantity: TextView = itemView.findViewById(R.id.item_quantity_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item_layout, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = cartItems[position]

        holder.itemName.text = currentItem.restaurantName
        holder.itemPrice.text = "$${currentItem.costPerBag}"
        holder.itemQuantity.text = "Qty: ${currentItem.quantity}"
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    fun updateCart() {
        cartItems = Cart.getItems()
        notifyDataSetChanged()
    }
}