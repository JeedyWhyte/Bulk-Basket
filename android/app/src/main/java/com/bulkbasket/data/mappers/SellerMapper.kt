package com.bulkbasket.data.mappers

import com.bulkbasket.data.remote.dto.SellerProfileDto
import com.bulkbasket.domain.model.Seller

fun SellerProfileDto.toSeller() = Seller(
    id = id,
    username = username,
    email = email,
    businessName = business_name,
    marketName = market_name,
    description = description,
    latitude = latitude,
    longitude = longitude,
    rating = rating,
    totalRatings = total_ratings,
    isOpen = is_open,
    openingTime = opening_time,
    closingTime = closing_time,
    products = products.map { it.toProduct() },
)