package com.bulkbasket.data.remote.dto

data class ReviewDto(
    val id: Int,
    val order_id: String,
    val seller: Int,
    val seller_name: String,
    val rating: Int,
    val comment: String,
    val created_at: String,
)

data class ReviewCreateRequest(
    val order_id: String,
    val rating: Int,
    val comment: String = "",
)
