package com.bulkbasket.domain.model

data class Delivery(
    val id: Int,
    val orderId: String,
    val orderTotal: String,
    val riderId: Int?,
    val riderName: String?,
    val status: String,
    val currentLatitude: String?,
    val currentLongitude: String?,
    val street: String?,
    val city: String?,
    val state: String?,
    val assignedAt: String?,
    val pickedUpAt: String?,
    val deliveredAt: String?,
    val createdAt: String,
)