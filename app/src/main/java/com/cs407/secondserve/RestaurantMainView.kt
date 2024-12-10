package com.cs407.secondserve

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cs407.secondserve.service.AccountService
import com.cs407.secondserve.service.RestaurantService
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
                val bagPrice = bagPriceText.toDoubleOrNull()
                if (bagPrice != null) {
                    RestaurantService.updateRestaurantInformation(
                        bagsAvailable = quantity,
                        bagPrice = bagPrice,
                        onSuccess = {
                            Toast.makeText(this, "Information updated successfully", Toast.LENGTH_SHORT).show()
                        },
                        onFailure = { reason ->
                            Toast.makeText(this, reason, Toast.LENGTH_SHORT).show()
                        },
                        onException = { exception ->
                            Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                        }
                    )
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

        updateViewWithRestaurantInfo(bagPriceInput, quantityText)
    }

    private fun updateViewWithRestaurantInfo(bagPriceInput: TextInputEditText, quantityText: TextView) {
        val user = AccountService.currentUser!!
        user.restaurant?.bagPrice?.let {
            bagPriceInput.setText(it.toString())
        }
        user.restaurant?.bagsAvailable.let {
            quantityText.text = it.toString()
            quantity = it ?: 0
        }

        RestaurantService.getRestaurantDashboardInformation(
            onSuccess = { info ->
                val earningsLast24HoursText = getString(R.string.restaurant_earnings, info.earningsLast24Hours)
                val earningsAllTime = getString(R.string.restaurant_earnings, info.earningsAllTime)
                findViewById<TextView>(R.id.orders_last_24_hours).text = info.ordersLast24Hours.toString()
                findViewById<TextView>(R.id.earnings_last_24_hours).text = earningsLast24HoursText
                findViewById<TextView>(R.id.orders_all_time).text = info.ordersAllTime.toString()
                findViewById<TextView>(R.id.earnings_all_time).text = earningsAllTime
            }
        )
    }
}
