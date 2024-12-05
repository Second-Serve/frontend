package com.cs407.secondserve.model

data class CartItem(
    val restaurantId: String,
    val restaurantName: String,
    val restaurantAddress: String,
    val costPerBag: Double,
    val quantity: Int
) {
    fun getTotalPrice(): Double {
        return costPerBag * quantity
    }
}