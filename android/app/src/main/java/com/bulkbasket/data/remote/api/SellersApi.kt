package com.bulkbasket.data.remote.api

import com.bulkbasket.data.remote.dto.ApiResponse
import com.bulkbasket.data.remote.dto.SellerProfileCreateRequest
import com.bulkbasket.data.remote.dto.SellerProfileDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface SellersApi {
    @GET("sellers/nearby/")
    suspend fun getNearbySellers(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radius: Double = 10.0,
    ): Response<ApiResponse<List<SellerProfileDto>>>

    @GET("sellers/{id}/")
    suspend fun getSellerDetail(
        @Path("id") id: Int,
    ): Response<SellerProfileDto>

    @POST("sellers/profile/")
    suspend fun createSellerProfile(
        @Body request: SellerProfileCreateRequest
    ): Response<ApiResponse<SellerProfileDto>>

    @PUT("sellers/profile/me/")
    suspend fun updateSellerProfile(
        @Body request: SellerProfileCreateRequest
    ): Response<SellerProfileDto>

    @GET("sellers/profile/me/")
    suspend fun getMySellerProfile(): Response<SellerProfileDto>
}
