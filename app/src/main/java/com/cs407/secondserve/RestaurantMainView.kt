package com.cs407.secondserve

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RestaurantMainView : AppCompatActivity() {
    private var quantity = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restaurant_main_page)

        val decreaseButton = findViewById<Button>(R.id.bag_decrease_button)
        val increaseButton = findViewById<Button>(R.id.bag_increase_button)
        val quantityText = findViewById<TextView>(R.id.bag_count)
        val bagPriceInput = findViewById<TextInputEditText>(R.id.bag_price_input)
        val updateBagPriceButton = findViewById<MaterialButton>(R.id.update_bag_price_button)

        updateBagPriceButton.setOnClickListener {
            val bagPriceText = bagPriceInput.text.toString()
            if (bagPriceText.isNotEmpty()) {
                val bagPrice = bagPriceText.toFloatOrNull()
                if (bagPrice != null) {
                    //save the price here
                } else {
                    Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a price", Toast.LENGTH_SHORT).show()
            }
        }

        decreaseButton.setOnClickListener {
            if (quantity > 0) {
                quantity--
                quantityText.text = quantity.toString()
            }
        }

        increaseButton.setOnClickListener {
            quantity++
            quantityText.text = quantity.toString()
        }
    }


}
