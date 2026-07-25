package com.bulkbasket.data.remote.api

import com.bulkbasket.data.remote.dto.OrderCreateRequest
import com.bulkbasket.data.remote.dto.OrderDto
import com.bulkbasket.data.remote.dto.OrderStatusRequest
import com.bulkbasket.data.remote.dto.ApiResponse
import com.bulkbasket.data.remote.dto.PaginatedResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface OrdersApi {
    @GET("orders/")
    suspend fun getOrders(): Response<PaginatedResponse<OrderDto>>

    @POST("orders/")
    suspend fun createOrder(@Body request: OrderCreateRequest): Response<ApiResponse<OrderDto>>

    @GET("orders/{id}/")
    suspend fun getOrder(@Path("id") id: String): Response<OrderDto>

    @GET("orders/seller/")
    suspend fun getSellerOrders(): Response<PaginatedResponse<OrderDto>>

    @PATCH("orders/seller/{id}/status/")
    suspend fun updateOrderStatus(
        @Path("id") id: String,
        @Body request: OrderStatusRequest,
    ): Response<ApiResponse<OrderDto>>
}