package com.bulkbasket.data.repository

import com.bulkbasket.data.mappers.toUser
import com.bulkbasket.data.remote.api.AuthApi
import com.bulkbasket.data.remote.dto.LoginRequest
import com.bulkbasket.data.remote.dto.RegisterRequest
import com.bulkbasket.domain.model.User
import com.bulkbasket.domain.repository.IAuthRepository
import com.bulkbasket.utils.NetworkResult
import com.bulkbasket.utils.PreferencesManager
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val prefs: PreferencesManager,
) : IAuthRepository {

    override suspend fun login(
        username: String,
        password: String,
    ): NetworkResult<User> {
        return try {
            val response = api.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                val body = response.body()!!
                prefs.saveTokens(body.access, body.refresh)

                val profileResponse = api.getProfile()
                if (profileResponse.isSuccessful) {
                    val user = profileResponse.body()!!.toUser()
                    prefs.saveUserInfo(
                        role = user.role,
                        username = user.username,
                        userId = user.id.toString(),
                    )
                    NetworkResult.Success(user)
                } else {
                    NetworkResult.Error("Failed to load profile")
                }
            } else {
                NetworkResult.Error("Invalid credentials", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String,
        role: String,
        phone: String,
    ): NetworkResult<User> {
        return try {
            val response = api.register(
                RegisterRequest(username, email, password, role, phone)
            )
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!.toUser())
            } else {
                NetworkResult.Error("Registration failed", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun logout() {
        prefs.clear()
    }
}