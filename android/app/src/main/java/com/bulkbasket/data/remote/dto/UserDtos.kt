package com.bulkbasket.data.remote.dto

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val role: String,
    val phone_number: String,
)

data class LoginRequest(
    val username: String,
    val password: String,
)

data class LoginResponse(
    val access: String,
    val refresh: String,
)

data class UserDto(
    val id: Int,
    val username: String,
    val email: String,
    val role: String,
    val phone_number: String,
    val avatar_url: String?,
    val is_verified: Boolean,
)