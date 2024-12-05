package com.cs407.secondserve
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(private val cartItems: List<List<Any>>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

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
        val itemName = currentItem[0] as String
        val itemPrice = currentItem[1] as Double
        val itemQuantity = currentItem[2] as Int

        holder.itemName.text = itemName
        holder.itemPrice.text = "$${itemPrice}"
        holder.itemQuantity.text = "Qty: $itemQuantity"
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
}