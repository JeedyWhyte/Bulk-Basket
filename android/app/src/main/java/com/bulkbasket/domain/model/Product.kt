package com.bulkbasket.domain.model

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: String,
    val unit: String,
    val minOrderQty: Int,
    val stockQuantity: Int,
    val imageUrl: String?,
    val isAvailable: Boolean,
    val inStock: Boolean,
    val categoryId: Int?,
    val categoryName: String?,
    val sellerId: Int,
    val sellerName: String,
)