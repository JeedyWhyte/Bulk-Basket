package com.bulkbasket.domain.model

data class RiderProfile(
    val id: Int,
    val username: String,
    val phoneNumber: String,
    val isAvailable: Boolean,
    val currentLatitude: String?,
    val currentLongitude: String?,
    val totalDeliveries: Int,
    val rating: String,
    val createdAt: String,
)