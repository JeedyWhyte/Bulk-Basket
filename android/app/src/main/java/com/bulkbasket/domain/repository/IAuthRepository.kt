package com.bulkbasket.domain.repository

import com.bulkbasket.domain.model.User
import com.bulkbasket.utils.NetworkResult

interface IAuthRepository {
    suspend fun login(username: String, password: String): NetworkResult<User>
    suspend fun register(
        username: String, email: String, password: String,
        role: String, phone: String,
    ): NetworkResult<User>
    suspend fun logout()
}