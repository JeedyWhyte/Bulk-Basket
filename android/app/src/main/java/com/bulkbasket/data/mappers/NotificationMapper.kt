package com.bulkbasket.data.mappers

import com.bulkbasket.data.remote.dto.NotificationDto
import com.bulkbasket.domain.model.Notification

fun NotificationDto.toNotification() = Notification(
    id = id,
    title = title,
    body = body,
    notificationType = notification_type,
    data = data,
    isRead = is_read,
    createdAt = created_at,
)