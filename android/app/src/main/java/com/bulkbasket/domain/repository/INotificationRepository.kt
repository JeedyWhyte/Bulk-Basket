package com.bulkbasket.domain.repository

import com.bulkbasket.domain.model.Notification
import com.bulkbasket.utils.NetworkResult

interface INotificationRepository {
    suspend fun getNotifications(): NetworkResult<List<Notification>>
    suspend fun getUnreadCount(): NetworkResult<Int>
    suspend fun markAsRead(id: Int): NetworkResult<Unit>
    suspend fun markAllAsRead(): NetworkResult<Unit>
}