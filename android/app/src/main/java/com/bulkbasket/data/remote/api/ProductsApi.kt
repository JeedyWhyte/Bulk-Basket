package com.bulkbasket.data.remote.api

import com.bulkbasket.data.remote.dto.CategoryDto
import com.bulkbasket.data.remote.dto.PaginatedResponse
import com.bulkbasket.data.remote.dto.ProductDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductsApi {
    @GET("products/")
    suspend fun getProducts(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("min_price") minPrice: Double? = null,
        @Query("max_price") maxPrice: Double? = null,
        @Query("page") page: Int = 1,
    ): Response<PaginatedResponse<ProductDto>>

    @GET("products/{id}/")
    suspend fun getProduct(@Path("id") id: Int): Response<ProductDto>

    @GET("products/categories/")
    suspend fun getCategories(): Response<List<CategoryDto>>
}