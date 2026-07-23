package com.bulkbasket.data.remote.dto

data class ProductDto(
    val id: Int,
    val name: String,
    val description: String,
    val price: String,
    val unit: String,
    val min_order_qty: Int,
    val stock_quantity: Int,
    val image_url: String?,
    val is_available: Boolean,
    val in_stock: Boolean,
    val category: Int?,
    val category_name: String?,
    val seller: Int,
    val seller_name: String,
    val created_at: String,
)

data class CategoryDto(
    val id: Int,
    val name: String,
    val slug: String,
    val icon_url: String?,
)

data class PaginatedResponse<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>,
)