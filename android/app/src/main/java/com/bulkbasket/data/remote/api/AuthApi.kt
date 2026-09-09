package com.bulkbasket.data.remote.api

import com.bulkbasket.data.remote.dto.AddressDto
import com.bulkbasket.data.remote.dto.AddressRequest
import com.bulkbasket.data.remote.dto.ApiResponse
import com.bulkbasket.data.remote.dto.FcmTokenRequest
import com.bulkbasket.data.remote.dto.LoginRequest
import com.bulkbasket.data.remote.dto.LoginResponse
import com.bulkbasket.data.remote.dto.PaginatedResponse
import com.bulkbasket.data.remote.dto.RefreshRequest
import com.bulkbasket.data.remote.dto.RefreshResponse
import com.bulkbasket.data.remote.dto.RegisterRequest
import com.bulkbasket.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {
    @POST("users/register/")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<UserDto>>

    @POST("users/login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("users/token/refresh/")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<RefreshResponse>

    @GET("users/profile/")
    suspend fun getProfile(): Response<UserDto>

    @PUT("users/profile/")
    suspend fun updateProfile(@Body data: Map<String, String>): Response<UserDto>

    @GET("users/addresses/")
    suspend fun getAddresses(): Response<PaginatedResponse<AddressDto>>

    @POST("users/addresses/")
    suspend fun createAddress(@Body request: AddressRequest): Response<AddressDto>

    @POST("users/fcm-token/")
    suspend fun updateFcmToken(@Body request: FcmTokenRequest): Response<ApiResponse<Unit>>

    @POST("users/close-account/")
    suspend fun closeAccount(): Response<ApiResponse<Unit>>
}
