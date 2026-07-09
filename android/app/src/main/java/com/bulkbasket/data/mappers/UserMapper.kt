package com.bulkbasket.data.mappers

import com.bulkbasket.data.remote.dto.UserDto
import com.bulkbasket.domain.model.User

fun UserDto.toUser() = User(
    id = id,
    username = username,
    email = email,
    role = role,
    phoneNumber = phone_number,
    avatarUrl = avatar_url,
    isVerified = is_verified,
)