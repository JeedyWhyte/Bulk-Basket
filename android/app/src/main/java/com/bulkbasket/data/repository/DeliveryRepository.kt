package com.bulkbasket.data.repository

import com.bulkbasket.data.mappers.toDelivery
import com.bulkbasket.data.remote.api.DeliveryApi
import com.bulkbasket.data.remote.dto.DeliveryStatusRequest
import com.bulkbasket.data.remote.dto.LocationUpdateRequest
import com.bulkbasket.data.mappers.toRiderProfile
import com.bulkbasket.data.remote.dto.RiderProfileRequest
import com.bulkbasket.domain.model.RiderProfile
import com.bulkbasket.domain.model.Delivery
import com.bulkbasket.domain.repository.IDeliveryRepository
import com.bulkbasket.utils.NetworkResult
import com.bulkbasket.utils.errorMessage
import javax.inject.Inject

class DeliveryRepository @Inject constructor(
    private val api: DeliveryApi,
) : IDeliveryRepository {

    override suspend fun getAvailableDeliveries(): NetworkResult<List<Delivery>> {
        return try {
            val response = api.getAvailableDeliveries()
            if (response.isSuccessful) {
                NetworkResult.Success(
                    response.body()!!.results.map { it.toDelivery() }
                )
            } else {
                NetworkResult.Error(
                    response.errorMessage("Failed to load deliveries"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun getActiveDeliveries(): NetworkResult<List<Delivery>> {
        return try {
            val response = api.getActiveDeliveries()
            if (response.isSuccessful) {
                NetworkResult.Success(
                    response.body()!!.results.map { it.toDelivery() }
                )
            } else {
                NetworkResult.Error(
                    response.errorMessage("Failed to load active deliveries"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun acceptDelivery(id: Int): NetworkResult<Delivery> {
        return try {
            val response = api.acceptDelivery(id)
            if (response.isSuccessful) {
                val delivery = response.body()?.data?.toDelivery()
                if (delivery != null) {
                    NetworkResult.Success(delivery)
                } else {
                    NetworkResult.Error("Failed to accept delivery — empty response")
                }
            } else {
                NetworkResult.Error(
                    response.errorMessage("Failed to accept delivery"),
                    response.code(),
                )
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
                val delivery = response.body()?.data?.toDelivery()
                if (delivery != null) {
                    NetworkResult.Success(delivery)
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
                NetworkResult.Error(
                    response.errorMessage("Failed to update location"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun getRiderProfile(): NetworkResult<RiderProfile> {
        return try {
            val response = api.getRiderProfile()
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!.toRiderProfile())
            } else {
                NetworkResult.Error(
                    response.errorMessage("Profile not found"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun createRiderProfile(
        isAvailable: Boolean
    ): NetworkResult<RiderProfile> {
        return try {
            val response = api.createRiderProfile(
                RiderProfileRequest(is_available = isAvailable)
            )
            if (response.isSuccessful) {
                val profile = response.body()?.data?.toRiderProfile()
                if (profile != null) {
                    NetworkResult.Success(profile)
                } else {
                    NetworkResult.Error("Failed to create profile — empty response")
                }
            } else {
                NetworkResult.Error(
                    response.errorMessage("Failed to create profile"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun updateRiderProfile(
        isAvailable: Boolean
    ): NetworkResult<RiderProfile> {
        return try {
            val response = api.updateRiderProfile(
                RiderProfileRequest(is_available = isAvailable)
            )
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!.toRiderProfile())
            } else {
                NetworkResult.Error(
                    response.errorMessage("Failed to update profile"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }
}
