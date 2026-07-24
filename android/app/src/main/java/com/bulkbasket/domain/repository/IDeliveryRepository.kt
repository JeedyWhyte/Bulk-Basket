package com.bulkbasket.domain.repository

import com.bulkbasket.domain.model.Delivery
import com.bulkbasket.utils.NetworkResult

interface IDeliveryRepository {
    suspend fun getAvailableDeliveries(): NetworkResult<List<Delivery>>
    suspend fun getActiveDeliveries(): NetworkResult<List<Delivery>>
    suspend fun acceptDelivery(id: Int): NetworkResult<Delivery>
    suspend fun updateDeliveryStatus(id: Int, status: String): NetworkResult<Delivery>
    suspend fun updateLocation(latitude: String, longitude: String): NetworkResult<Unit>
}