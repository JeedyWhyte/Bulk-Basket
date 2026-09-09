package com.bulkbasket.data.repository

import com.bulkbasket.data.mappers.toReview
import com.bulkbasket.data.remote.api.ReviewsApi
import com.bulkbasket.data.remote.dto.ReviewCreateRequest
import com.bulkbasket.domain.model.Review
import com.bulkbasket.domain.repository.IReviewRepository
import com.bulkbasket.utils.NetworkResult
import com.bulkbasket.utils.errorMessage
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val api: ReviewsApi,
) : IReviewRepository {

    override suspend fun getMyReviews(): NetworkResult<List<Review>> {
        return try {
            val response = api.getMyReviews()
            if (response.isSuccessful) {
                NetworkResult.Success(
                    response.body()!!.results.map { it.toReview() }
                )
            } else {
                NetworkResult.Error(
                    response.errorMessage("Failed to load reviews"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun createReview(
        orderId: String,
        rating: Int,
        comment: String,
    ): NetworkResult<Review> {
        return try {
            val response = api.createReview(
                ReviewCreateRequest(order_id = orderId, rating = rating, comment = comment)
            )
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!.data!!.toReview())
            } else {
                NetworkResult.Error(
                    response.errorMessage("Failed to submit review"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }
}
