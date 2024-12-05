package com.cs407.secondserve.model

class Cart {
    companion object {
        private var items: List<CartItem> = mutableListOf()

        fun addRestaurantToCart(restaurant: Restaurant, count: Int) {
            if (count <= 0) {
                throw IllegalArgumentException("Count must be greater than 0")
            }

            items += CartItem(restaurant, count)
        }

        fun removeRestaurantFromCart(restaurant: Restaurant) {
            for (item in items) {
                if (item.restaurant == restaurant) {
                    items -= item
                    return
                }
            }
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
    }
}