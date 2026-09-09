package com.bulkbasket.data.remote.api

import com.bulkbasket.data.remote.dto.ApiResponse
import com.bulkbasket.data.remote.dto.NotificationDto
import com.bulkbasket.data.remote.dto.PaginatedResponse
import com.bulkbasket.data.remote.dto.UnreadCountDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface NotificationsApi {
    @GET("notifications/")
    suspend fun getNotifications(): Response<PaginatedResponse<NotificationDto>>

    @GET("notifications/unread/")
    suspend fun getUnreadCount(): Response<ApiResponse<UnreadCountDto>>

    @PATCH("notifications/{id}/read/")
    suspend fun markAsRead(@Path("id") id: Int): Response<Unit>

    @PATCH("notifications/mark-all-read/")
    suspend fun markAllAsRead(): Response<Unit>
}
