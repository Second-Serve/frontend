package com.cs407.secondserve

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RestaurantViewAdapter : RecyclerView.Adapter<RestaurantViewAdapter.UserViewHolder>() {

    private val userList = mutableListOf<String>()

    fun submitList(users: List<String>) {
        userList.clear()
        userList.addAll(users)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int = userList.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(user: String) {
            textView.text = user
        }
    }
}