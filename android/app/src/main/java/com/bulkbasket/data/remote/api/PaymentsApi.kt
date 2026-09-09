package com.bulkbasket.data.remote.api

import com.bulkbasket.data.remote.dto.ApiResponse
import com.bulkbasket.data.remote.dto.ChargeOrderRequest
import com.bulkbasket.data.remote.dto.PaginatedResponse
import com.bulkbasket.data.remote.dto.PaymentDto
import com.bulkbasket.data.remote.dto.PaymentMethodDto
import com.bulkbasket.data.remote.dto.SavePaymentMethodRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentsApi {
    @GET("payments/methods/")
    suspend fun getPaymentMethods(): Response<PaginatedResponse<PaymentMethodDto>>

    @POST("payments/methods/")
    suspend fun addPaymentMethod(
        @Body request: SavePaymentMethodRequest,
    ): Response<PaymentMethodDto>

    @DELETE("payments/methods/{id}/")
    suspend fun deletePaymentMethod(@Path("id") id: Int): Response<Unit>

    @POST("payments/charge/")
    suspend fun chargeOrder(
        @Body request: ChargeOrderRequest,
    ): Response<ApiResponse<PaymentDto>>
}
