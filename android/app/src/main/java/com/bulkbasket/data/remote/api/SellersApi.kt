package com.bulkbasket.data.remote.api

import com.bulkbasket.data.remote.dto.SellerProfileDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SellersApi {
    @GET("sellers/nearby/")
    suspend fun getNearbySellers(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radius: Double = 10.0,
    ): Response<List<SellerProfileDto>>

    @GET("sellers/{id}/")
    suspend fun getSellerDetail(
        @Path("id") id: Int,
    ): Response<SellerProfileDto>
}