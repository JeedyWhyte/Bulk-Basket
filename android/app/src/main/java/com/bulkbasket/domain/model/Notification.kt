package com.bulkbasket.domain.model

data class Notification(
    val id: Int,
    val title: String,
    val body: String,
    val notificationType: String,
    val data: Map<String, String>,
    val isRead: Boolean,
    val createdAt: String,
)