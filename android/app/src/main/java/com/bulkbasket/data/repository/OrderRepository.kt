package com.bulkbasket.data.repository

import com.bulkbasket.data.mappers.toOrder
import com.bulkbasket.data.remote.api.OrdersApi
import com.bulkbasket.data.remote.dto.OrderCreateRequest
import com.bulkbasket.data.remote.dto.OrderStatusRequest
import com.bulkbasket.domain.model.Order
import com.bulkbasket.domain.repository.IOrderRepository
import com.bulkbasket.utils.NetworkResult
import com.bulkbasket.utils.errorMessage
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val api: OrdersApi,
) : IOrderRepository {

    override suspend fun getOrders(): NetworkResult<List<Order>> {
        return try {
            val response = api.getOrders()
            if (response.isSuccessful) {
                NetworkResult.Success(
                    response.body()!!.results.map { it.toOrder() }
                )
            } else {
                NetworkResult.Error(
                    response.errorMessage("Failed to load orders"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun getOrder(id: String): NetworkResult<Order> {
        return try {
            val response = api.getOrder(id)
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!.toOrder())
            } else {
                NetworkResult.Error(
                    response.errorMessage("Order not found"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun createOrder(
        request: OrderCreateRequest
    ): NetworkResult<Order> {
        return try {
            val response = api.createOrder(request)
            if (response.isSuccessful) {
                val order = response.body()?.data?.toOrder()
                if (order != null) {
                    NetworkResult.Success(order)
                } else {
                    NetworkResult.Error("Failed to place order — empty response")
                }
            } else {
                NetworkResult.Error(
                    response.errorMessage("Failed to place order"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }


    override suspend fun getSellerOrders(): NetworkResult<List<Order>> {
        return try {
            val response = api.getSellerOrders()
            if (response.isSuccessful) {
                NetworkResult.Success(
                    response.body()!!.results.map { it.toOrder() }
                )
            } else {
                NetworkResult.Error(
                    response.errorMessage("Failed to load orders"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun updateOrderStatus(
        id: String,
        status: String,
    ): NetworkResult<Order> {
        return try {
            val response = api.updateOrderStatus(id, OrderStatusRequest(status))
            if (response.isSuccessful) {
                val order = response.body()?.data?.toOrder()
                if (order != null) {
                    NetworkResult.Success(order)
                } else {
                    NetworkResult.Error("Failed to update status — empty response")
                }
            } else {
                NetworkResult.Error(
                    response.errorMessage("Failed to update status"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }
}