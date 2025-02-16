package com.example.halidao.data.model

data class OrderItem(
    val foodName: String,
    val quantity: Int,
    val unitPrice: Int
) {
    fun getTotalPrice(): Int {
        return quantity * unitPrice
    }
}
