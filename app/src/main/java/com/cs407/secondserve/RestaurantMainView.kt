package com.cs407.secondserve

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs407.secondserve.R


class RestaurantMainView : AppCompatActivity() {

    private lateinit var bagPriceInput: EditText
    private lateinit var setPriceButton: Button
    private lateinit var userListRecyclerView: RecyclerView
    private lateinit var userAdapter: RestaurantViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restaurant_main_page)

//        bagPriceInput = findViewById(R.id.bag_price_input)
//        setPriceButton = findViewById(R.id.set_price_button)
//        userListRecyclerView = findViewById(R.id.user_list_recycler_view)

        userListRecyclerView.layoutManager = LinearLayoutManager(this)
        userAdapter = RestaurantViewAdapter()
        userListRecyclerView.adapter = userAdapter

        fetchUsers()

        setPriceButton.setOnClickListener {
            val price = bagPriceInput.text.toString()
            if (price.isNotEmpty()) {
                setBagPrice(price.toDouble())
            } else {
                Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchUsers() {
        //fill this to get the users from backend
    }

    private fun setBagPrice(price: Double) {
        //send price to backend
        Toast.makeText(this, "Bag price set to $price", Toast.LENGTH_SHORT).show()
    }
}