package com.cs407.secondserve.model

import com.cs407.secondserve.CartAdapter

class Cart {

    companion object {
        private val items: MutableList<CartItem> = mutableListOf()
        private var cartAdapter: CartAdapter? = null

        fun setAdapter(adapter: CartAdapter) {
            cartAdapter = adapter
        }

        fun addItemToCart(item: CartItem) {
            items.add(item)
            cartAdapter?.updateCart()
        }

        fun removeItemFromCart(item: CartItem) {
            items.remove(item)
            cartAdapter?.updateCart()
        }

        fun getItems(): List<CartItem> {
            return items
        }

        fun getTotalPrice(): Double {
            return items.sumOf { it.getTotalPrice() }
        }

        fun toMap(): MutableMap<String, Any> {
            val itemsMap = items.map {
                mutableMapOf(
                    "restaurantId" to it.restaurantId,
                    "quantity" to it.quantity
                )
            }
            return mutableMapOf("items" to itemsMap)
        }
    }
}