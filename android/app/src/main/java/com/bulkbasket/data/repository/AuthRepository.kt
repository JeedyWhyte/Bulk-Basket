package com.bulkbasket.data.repository

import com.bulkbasket.data.mappers.toUser
import com.bulkbasket.data.remote.api.AuthApi
import com.bulkbasket.data.remote.dto.AddressRequest
import com.bulkbasket.data.remote.dto.FcmTokenRequest
import com.bulkbasket.data.remote.dto.LoginRequest
import com.bulkbasket.data.remote.dto.RegisterRequest
import com.bulkbasket.data.mappers.toAddress
import com.bulkbasket.domain.model.Address
import com.bulkbasket.domain.model.User
import com.bulkbasket.domain.repository.IAuthRepository
import com.bulkbasket.utils.NetworkResult
import com.bulkbasket.utils.PreferencesManager
import com.bulkbasket.utils.errorMessage
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

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
                        role = user.role ?: "",
                        username = user.username ?: "",
                        userId = user.id.toString(),
                    )
                    // Fire-and-forget: must never delay a successful login.
                    @OptIn(DelicateCoroutinesApi::class)
                    GlobalScope.launch { registerFcmToken() }
                    NetworkResult.Success(user)
                } else {
                    NetworkResult.Error(
                        profileResponse.errorMessage("Failed to load profile"),
                        profileResponse.code(),
                    )
                }
            } else {
                NetworkResult.Error(
                    response.errorMessage("Invalid credentials"),
                    response.code(),
                )
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
                val user = response.body()?.data?.toUser()
                if (user != null) {
                    NetworkResult.Success(user)
                } else {
                    NetworkResult.Error("Registration failed — empty response")
                }
            } else {
                NetworkResult.Error(
                    response.errorMessage("Registration failed"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun getProfile(): NetworkResult<User> {
        return try {
            val response = api.getProfile()
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!.toUser())
            } else {
                NetworkResult.Error(
                    response.errorMessage("Failed to load profile"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun getAddresses(): NetworkResult<List<Address>> {
        return try {
            val response = api.getAddresses()
            if (response.isSuccessful) {
                NetworkResult.Success(
                    response.body()!!.results.map { it.toAddress() }
                )
            } else {
                NetworkResult.Error(
                    response.errorMessage("Failed to load addresses"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun createAddress(
        label: String,
        street: String,
        city: String,
        state: String,
        isDefault: Boolean,
    ): NetworkResult<Address> {
        return try {
            val response = api.createAddress(
                AddressRequest(
                    label = label,
                    street = street,
                    city = city,
                    state = state,
                    latitude = null,
                    longitude = null,
                    is_default = isDefault,
                )
            )
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!.toAddress())
            } else {
                NetworkResult.Error(
                    response.errorMessage("Failed to save address"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun updateFcmToken(token: String): NetworkResult<Unit> {
        return try {
            val response = api.updateFcmToken(FcmTokenRequest(token))
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error(
                    response.errorMessage("Failed to register device"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun logout() {
        prefs.clear()
    }

    /**
     * Best-effort upload of the device's FCM token right after login so the
     * backend can send this user push notifications. Failures are ignored —
     * they must never block a successful login.
     */
    private suspend fun registerFcmToken() {
        try {
            val token = suspendCancellableCoroutine<String?> { cont ->
                FirebaseMessaging.getInstance().token
                    .addOnSuccessListener { if (cont.isActive) cont.resume(it) }
                    .addOnFailureListener { if (cont.isActive) cont.resume(null) }
            }
            if (!token.isNullOrEmpty()) {
                api.updateFcmToken(FcmTokenRequest(token))
            }
        } catch (_: Exception) {
            // Push registration is optional; the FcmService retries on
            // the next token rotation.
        }
    }
}
