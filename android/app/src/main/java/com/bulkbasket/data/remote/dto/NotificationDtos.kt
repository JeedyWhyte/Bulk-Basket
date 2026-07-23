package com.bulkbasket.data.remote.dto

data class NotificationDto(
    val id: Int,
    val title: String,
    val body: String,
    val notification_type: String,
    val data: Map<String, String>,
    val is_read: Boolean,
    val created_at: String,
)

data class UnreadCountDto(
    val unread_count: Int,
)