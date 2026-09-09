package com.bulkbasket.domain.repository

import com.bulkbasket.domain.model.User
import com.bulkbasket.utils.NetworkResult
import com.bulkbasket.domain.model.Address

interface IAuthRepository {
    suspend fun login(username: String, password: String): NetworkResult<User>
    suspend fun register(
        username: String,
        email: String,
        password: String,
        role: String,
        phone: String,
    ): NetworkResult<User>
    suspend fun logout()
    suspend fun closeAccount(): NetworkResult<Unit>
    suspend fun getProfile(): NetworkResult<User>
    suspend fun updateProfile(
        username: String,
        email: String,
        phoneNumber: String,
    ): NetworkResult<User>
    suspend fun getAddresses(): NetworkResult<List<Address>>
    suspend fun createAddress(
        label: String,
        street: String,
        city: String,
        state: String,
        isDefault: Boolean,
    ): NetworkResult<Address>
    suspend fun updateFcmToken(token: String): NetworkResult<Unit>
}