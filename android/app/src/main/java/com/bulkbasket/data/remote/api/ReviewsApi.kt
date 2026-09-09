package com.bulkbasket.data.remote.api

import com.bulkbasket.data.remote.dto.ApiResponse
import com.bulkbasket.data.remote.dto.PaginatedResponse
import com.bulkbasket.data.remote.dto.ReviewCreateRequest
import com.bulkbasket.data.remote.dto.ReviewDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ReviewsApi {
    @GET("reviews/")
    suspend fun getMyReviews(): Response<PaginatedResponse<ReviewDto>>

    @POST("reviews/")
    suspend fun createReview(@Body request: ReviewCreateRequest): Response<ApiResponse<ReviewDto>>
}
