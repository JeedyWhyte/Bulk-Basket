package com.bulkbasket.data.mappers

import com.bulkbasket.data.remote.dto.RiderProfileDto
import com.bulkbasket.domain.model.RiderProfile

fun RiderProfileDto.toRiderProfile() = RiderProfile(
    id = id,
    username = username,
    phoneNumber = phone_number,
    isAvailable = is_available,
    currentLatitude = current_latitude,
    currentLongitude = current_longitude,
    totalDeliveries = total_deliveries,
    rating = rating,
    createdAt = created_at,
)