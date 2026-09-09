package com.bulkbasket.data.mappers

import com.bulkbasket.data.remote.dto.CategoryDto
import com.bulkbasket.domain.model.Category

fun CategoryDto.toCategory() = Category(
    id = id,
    name = name,
    slug = slug,
    iconUrl = icon_url,
)
