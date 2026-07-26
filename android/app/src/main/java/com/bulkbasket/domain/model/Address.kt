package com.bulkbasket.domain.model

data class Address(
    val id: Int,
    val label: String,
    val street: String,
    val city: String,
    val state: String,
    val latitude: String?,
    val longitude: String?,
    val isDefault: Boolean,
)