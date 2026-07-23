package com.bulkbasket.domain.model

data class Seller(
    val id: Int,
    val username: String,
    val email: String,
    val businessName: String,
    val marketName: String,
    val description: String,
    val latitude: String?,
    val longitude: String?,
    val rating: String,
    val totalRatings: Int,
    val isOpen: Boolean,
    val openingTime: String?,
    val closingTime: String?,
    val products: List<Product>,
)