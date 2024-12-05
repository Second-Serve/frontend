package com.cs407.secondserve.model

data class CartItem(
    val restaurant: Restaurant,
    val quantity: Int
) {
    fun getTotalPrice(): Double {
        return (restaurant.bagPrice ?: 0.0) * quantity
    }
}