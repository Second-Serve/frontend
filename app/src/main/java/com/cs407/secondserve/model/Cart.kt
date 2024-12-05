package com.cs407.secondserve.model

class Cart {
    companion object {
        private var items: List<CartItem> = mutableListOf()

        fun addItemToCart(item: CartItem) {
            items += item
        }

        fun removeItemFromCart(item: CartItem) {
            items -= item
        }

        fun getItems(): List<CartItem> {
            return items
        }

        fun getTotalPrice(): Double {
            var totalPrice = 0.0
            for (item in items) {
                totalPrice += item.getTotalPrice()
            }
            return totalPrice
        }

        fun toMap(): MutableMap<String, Any> {
            val items = mutableListOf<MutableMap<String, Any>>()
            for (item in getItems()) {
                val itemMap = mutableMapOf<String, Any>(
                    "restaurantId" to item.restaurantId,
                    "quantity" to item.quantity
                )
                items.add(itemMap)
            }

            val map = mutableMapOf<String, Any>(
                "items" to items
            )
            return map
        }
    }
}