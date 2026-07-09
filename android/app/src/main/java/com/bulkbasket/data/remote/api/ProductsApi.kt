package com.bulkbasket.data.remote.api

import com.bulkbasket.data.remote.dto.ProductDto
import retrofit2.Response
import retrofit2.http.*

interface ProductsApi {
    @GET("products/")
    suspend fun getProducts(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1,
    ): Response<PaginatedResponse<ProductDto>>

    @GET("products/{id}/")
    suspend fun getProduct(@Path("id") id: Int): Response<ProductDto>

    @POST("products/")
    suspend fun createProduct(@Body product: ProductDto): Response<ProductDto>
}

data class PaginatedResponse<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>,
)