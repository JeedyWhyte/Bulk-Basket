package com.bulkbasket.ui.rider.activedelivery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulkbasket.domain.model.Delivery
import com.bulkbasket.domain.repository.IDeliveryRepository
import com.bulkbasket.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActiveDeliveryState(
    val isLoading: Boolean = false,
    val delivery: Delivery? = null,
    val error: String? = null,
    val successMessage: String? = null,
    val isCompleted: Boolean = false,
)

@HiltViewModel
class ActiveDeliveryViewModel @Inject constructor(
    private val deliveryRepository: IDeliveryRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val deliveryId: Int = checkNotNull(
        savedStateHandle["deliveryId"]
    )

    private val _state = MutableStateFlow(ActiveDeliveryState())
    val state: StateFlow<ActiveDeliveryState> = _state

    init {
        loadActiveDeliveries()
    }

    private fun loadActiveDeliveries() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = deliveryRepository.getActiveDeliveries()) {
                is NetworkResult.Success -> {
                    val delivery = result.data.firstOrNull {
                        it.id == deliveryId
                    }
                    _state.value = _state.value.copy(
                        isLoading = false,
                        delivery = delivery,
                    )
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message,
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun updateStatus(newStatus: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = deliveryRepository.updateDeliveryStatus(
                deliveryId, newStatus
            )) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        delivery = result.data,
                        successMessage = "Status updated to ${newStatus.replace("_", " ")}.",
                        isCompleted = newStatus == "delivered",
                    )
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message,
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun updateLocation(latitude: String, longitude: String) {
        viewModelScope.launch {
            deliveryRepository.updateLocation(latitude, longitude)
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(
            error = null,
            successMessage = null,
        )
    }
}