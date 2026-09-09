package com.bulkbasket.data.repository

import com.bulkbasket.data.mappers.toNotification
import com.bulkbasket.data.remote.api.NotificationsApi
import com.bulkbasket.domain.model.Notification
import com.bulkbasket.domain.repository.INotificationRepository
import com.bulkbasket.utils.NetworkResult
import com.bulkbasket.utils.errorMessage
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val api: NotificationsApi,
) : INotificationRepository {

    override suspend fun getNotifications(): NetworkResult<List<Notification>> {
        return try {
            val response = api.getNotifications()
            if (response.isSuccessful) {
                NetworkResult.Success(
                    response.body()!!.results.map { it.toNotification() }
                )
            } else {
                NetworkResult.Error(
                    response.errorMessage("Failed to load notifications"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun getUnreadCount(): NetworkResult<Int> {
        return try {
            val response = api.getUnreadCount()
            if (response.isSuccessful) {
                NetworkResult.Success(
                    response.body()?.data?.unread_count ?: 0
                )
            } else {
                NetworkResult.Error(
                    response.errorMessage("Failed to get unread count"),
                    response.code(),
                )
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
                NetworkResult.Error(
                    response.errorMessage("Failed to mark as read"),
                    response.code(),
                )
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
                NetworkResult.Error(
                    response.errorMessage("Failed to mark all as read"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }
}
