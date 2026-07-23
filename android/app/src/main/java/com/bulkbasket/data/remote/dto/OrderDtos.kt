package com.bulkbasket.data.remote.dto

data class OrderItemDto(
    val id: Int,
    val product: Int,
    val product_name: String,
    val product_unit: String,
    val quantity: Int,
    val unit_price: String,
    val total_price: String,
)

data class OrderDto(
    val id: String,
    val buyer: Int,
    val buyer_name: String,
    val seller: Int,
    val seller_name: String,
    val status: String,
    val subtotal: String,
    val delivery_fee: String,
    val total: String,
    val notes: String,
    val items: List<OrderItemDto>,
    val created_at: String,
    val updated_at: String,
)

data class OrderItemRequest(
    val product_id: Int,
    val quantity: Int,
)

data class OrderCreateRequest(
    val seller_id: Int,
    val delivery_address_id: Int,
    val items: List<OrderItemRequest>,
    val notes: String = "",
)

data class OrderStatusRequest(
    val status: String,
)