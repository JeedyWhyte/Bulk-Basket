package com.bulkbasket.data.remote.dto

data class SellerProfileDto(
    val id: Int,
    val username: String,
    val email: String,
    val business_name: String,
    val market_name: String,
    val description: String,
    val latitude: String?,
    val longitude: String?,
    val rating: String,
    val total_ratings: Int,
    val is_open: Boolean,
    val opening_time: String?,
    val closing_time: String?,
    val products: List<ProductDto>,
    val created_at: String,
)

data class NearbySellerRequest(
    val lat: Double,
    val lng: Double,
    val radius: Double = 10.0,
)