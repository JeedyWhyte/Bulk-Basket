package com.bulkbasket.data.remote.api

import com.bulkbasket.data.remote.dto.AddressDto
import com.bulkbasket.data.remote.dto.AddressRequest
import com.bulkbasket.data.remote.dto.LoginRequest
import com.bulkbasket.data.remote.dto.LoginResponse
import com.bulkbasket.data.remote.dto.RegisterRequest
import com.bulkbasket.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {
    @POST("users/register/")
    suspend fun register(@Body request: RegisterRequest): Response<UserDto>

    @POST("users/login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("users/profile/")
    suspend fun getProfile(): Response<UserDto>

    @PUT("users/profile/")
    suspend fun updateProfile(@Body data: Map<String, String>): Response<UserDto>

    @GET("users/addresses/")
    suspend fun getAddresses(): Response<List<AddressDto>>

    @POST("users/addresses/")
    suspend fun createAddress(@Body request: AddressRequest): Response<AddressDto>
}