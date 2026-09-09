package com.bulkbasket.domain.repository

import com.bulkbasket.domain.model.Review
import com.bulkbasket.utils.NetworkResult

interface IReviewRepository {
    suspend fun getMyReviews(): NetworkResult<List<Review>>
    suspend fun createReview(
        orderId: String,
        rating: Int,
        comment: String,
    ): NetworkResult<Review>
}
