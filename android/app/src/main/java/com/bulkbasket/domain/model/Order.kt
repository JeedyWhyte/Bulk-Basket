package com.bulkbasket.domain.model

data class OrderItem(
    val id: Int,
    val productId: Int,
    val productName: String,
    val productUnit: String,
    val quantity: Int,
    val unitPrice: String,
    val totalPrice: String,
)

data class Order(
    val id: String,
    val buyerId: Int,
    val buyerName: String,
    val sellerId: Int,
    val sellerName: String,
    val status: String,
    val subtotal: String,
    val deliveryFee: String,
    val total: String,
    val notes: String,
    val items: List<OrderItem>,
    val createdAt: String,
    val updatedAt: String,
)