package com.bulkbasket.data.remote.api

import com.bulkbasket.data.remote.dto.DeliveryDto
import com.bulkbasket.data.remote.dto.DeliveryStatusRequest
import com.bulkbasket.data.remote.dto.LocationUpdateRequest
import com.bulkbasket.data.remote.dto.RiderProfileDto
import com.bulkbasket.data.remote.dto.RiderProfileRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface DeliveryApi {
    @GET("delivery/available/")
    suspend fun getAvailableDeliveries(): Response<List<DeliveryDto>>

    @GET("delivery/active/")
    suspend fun getActiveDeliveries(): Response<List<DeliveryDto>>

    @POST("delivery/{id}/accept/")
    suspend fun acceptDelivery(@Path("id") id: Int): Response<DeliveryDto>

    @PATCH("delivery/{id}/status/")
    suspend fun updateDeliveryStatus(
        @Path("id") id: Int,
        @Body request: DeliveryStatusRequest,
    ): Response<DeliveryDto>

    @PATCH("delivery/location/")
    suspend fun updateLocation(
        @Body request: LocationUpdateRequest,
    ): Response<Unit>

    @POST("delivery/profile/")
    suspend fun createRiderProfile(
        @Body request: RiderProfileRequest,
    ): Response<RiderProfileDto>

    @GET("delivery/profile/me/")
    suspend fun getRiderProfile(): Response<RiderProfileDto>
}