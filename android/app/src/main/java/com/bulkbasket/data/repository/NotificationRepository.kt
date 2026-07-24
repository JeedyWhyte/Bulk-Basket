package com.bulkbasket.data.repository

import com.bulkbasket.data.mappers.toNotification
import com.bulkbasket.data.remote.api.NotificationsApi
import com.bulkbasket.domain.model.Notification
import com.bulkbasket.domain.repository.INotificationRepository
import com.bulkbasket.utils.NetworkResult
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val api: NotificationsApi,
) : INotificationRepository {

    override suspend fun getNotifications(): NetworkResult<List<Notification>> {
        return try {
            val response = api.getNotifications()
            if (response.isSuccessful) {
                NetworkResult.Success(
                    response.body()!!.map { it.toNotification() }
                )
            } else {
                NetworkResult.Error("Failed to load notifications", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun getUnreadCount(): NetworkResult<Int> {
        return try {
            val response = api.getUnreadCount()
            if (response.isSuccessful) {
                val count = response.body()
                    ?.get("data") as? Map<*, *>
                val unread = (count?.get("unread_count") as? Double)?.toInt() ?: 0
                NetworkResult.Success(unread)
            } else {
                NetworkResult.Error("Failed to get unread count", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun markAsRead(id: Int): NetworkResult<Unit> {
        return try {
            val response = api.markAsRead(id)
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("Failed to mark as read", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun markAllAsRead(): NetworkResult<Unit> {
        return try {
            val response = api.markAllAsRead()
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("Failed to mark all as read", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }
}