package com.bulkbasket.domain.model

data class Review(
    val id: Int,
    val orderId: String,
    val sellerId: Int,
    val sellerName: String,
    val rating: Int,
    val comment: String,
    val createdAt: String,
)
