package com.bulkbasket.data.mappers

import com.bulkbasket.data.remote.dto.ReviewDto
import com.bulkbasket.domain.model.Review

fun ReviewDto.toReview() = Review(
    id = id,
    orderId = order_id,
    sellerId = seller,
    sellerName = seller_name,
    rating = rating,
    comment = comment,
    createdAt = created_at,
)
