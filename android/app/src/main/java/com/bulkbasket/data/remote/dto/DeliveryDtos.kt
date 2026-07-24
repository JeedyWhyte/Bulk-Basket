package com.bulkbasket.data.remote.dto

data class DeliveryDto(
    val id: Int,
    val order: String,
    val order_total: String,
    val rider: Int?,
    val rider_name: String?,
    val status: String,
    val current_latitude: String?,
    val current_longitude: String?,
    val delivery_address: DeliveryAddressDto?,
    val assigned_at: String?,
    val picked_up_at: String?,
    val delivered_at: String?,
    val created_at: String,
)

data class DeliveryAddressDto(
    val street: String,
    val city: String,
    val state: String,
)

data class RiderProfileDto(
    val id: Int,
    val username: String,
    val phone_number: String,
    val is_available: Boolean,
    val current_latitude: String?,
    val current_longitude: String?,
    val total_deliveries: Int,
    val rating: String,
    val created_at: String,
)

data class RiderProfileRequest(
    val is_available: Boolean,
    val current_latitude: String? = null,
    val current_longitude: String? = null,
)

data class DeliveryStatusRequest(
    val status: String,
)

data class LocationUpdateRequest(
    val latitude: String,
    val longitude: String,
)