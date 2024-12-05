package com.cs407.secondserve.model

class Cart {
    companion object {
        private var items: List<CartItem> = mutableListOf()

        fun addRestaurantToCart(restaurant: Restaurant, count: Int) {
            if (count <= 0) {
                throw IllegalArgumentException("Count must be greater than 0")
            }

            val cartItem = CartItem(
                restaurantId = restaurant.id,
                restaurantName = restaurant.name,
                restaurantAddress = restaurant.address,
                costPerBag = restaurant.bagPrice ?: 0.0,
                quantity = count
            )
            addItemToCart(cartItem)
        }

        fun addItemToCart(item: CartItem) {
            items += item
        }

        fun removeRestaurantFromCart(restaurant: Restaurant) {
            for (item in items) {
                if (item.restaurantId == restaurant.id) {
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