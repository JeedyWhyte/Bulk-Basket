package com.bulkbasket.data.repository

import com.bulkbasket.data.mappers.toDelivery
import com.bulkbasket.data.remote.api.DeliveryApi
import com.bulkbasket.data.remote.dto.DeliveryStatusRequest
import com.bulkbasket.data.remote.dto.LocationUpdateRequest
import com.bulkbasket.domain.model.Delivery
import com.bulkbasket.domain.repository.IDeliveryRepository
import com.bulkbasket.utils.NetworkResult
import javax.inject.Inject

class DeliveryRepository @Inject constructor(
    private val api: DeliveryApi,
) : IDeliveryRepository {

    override suspend fun getAvailableDeliveries(): NetworkResult<List<Delivery>> {
        return try {
            val response = api.getAvailableDeliveries()
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!.map { it.toDelivery() })
            } else {
                NetworkResult.Error("Failed to load deliveries", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun getActiveDeliveries(): NetworkResult<List<Delivery>> {
        return try {
            val response = api.getActiveDeliveries()
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!.map { it.toDelivery() })
            } else {
                NetworkResult.Error("Failed to load active deliveries", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun acceptDelivery(id: Int): NetworkResult<Delivery> {
        return try {
            val response = api.acceptDelivery(id)
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!.toDelivery())
            } else {
                NetworkResult.Error("Failed to accept delivery", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun updateDeliveryStatus(
        id: Int,
        status: String,
    ): NetworkResult<Delivery> {
        return try {
            val response = api.updateDeliveryStatus(id, DeliveryStatusRequest(status))
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!.toDelivery())
            } else {
                NetworkResult.Error("Failed to update status", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun updateLocation(
        latitude: String,
        longitude: String,
    ): NetworkResult<Unit> {
        return try {
            val response = api.updateLocation(
                LocationUpdateRequest(latitude, longitude)
            )
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("Failed to update location", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }
}