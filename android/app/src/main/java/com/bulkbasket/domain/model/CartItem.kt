package com.bulkbasket.domain.model

data class CartItem(
    val product: Product,
    val quantity: Int,
    val sellerId: Int,
    val sellerName: String,
) {
    val lineTotal: Double
        get() = product.price.toDouble() * quantity
}