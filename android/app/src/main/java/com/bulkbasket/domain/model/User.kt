package com.bulkbasket.domain.model

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val role: String,
    val phoneNumber: String,
    val avatarUrl: String?,
    val isVerified: Boolean,
)