package com.bulkbasket.data.mappers

import com.bulkbasket.data.remote.dto.AddressDto
import com.bulkbasket.domain.model.Address

fun AddressDto.toAddress() = Address(
    id = id,
    label = label,
    street = street,
    city = city,
    state = state,
    latitude = latitude,
    longitude = longitude,
    isDefault = is_default,
)