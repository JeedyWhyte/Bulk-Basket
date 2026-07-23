package com.bulkbasket.domain.repository

import com.bulkbasket.domain.model.Order
import com.bulkbasket.data.remote.dto.OrderCreateRequest
import com.bulkbasket.utils.NetworkResult

interface IOrderRepository {
    suspend fun getOrders(): NetworkResult<List<Order>>
    suspend fun getOrder(id: String): NetworkResult<Order>
    suspend fun createOrder(request: OrderCreateRequest): NetworkResult<Order>
    suspend fun getSellerOrders(): NetworkResult<List<Order>>
    suspend fun updateOrderStatus(id: String, status: String): NetworkResult<Order>
}