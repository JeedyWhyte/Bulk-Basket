package com.bulkbasket.ui.rider.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulkbasket.domain.model.Delivery
import com.bulkbasket.domain.repository.IDeliveryRepository
import com.bulkbasket.utils.NetworkResult
import com.bulkbasket.utils.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RiderJobsState(
    val isLoading: Boolean = false,
    val availableDeliveries: List<Delivery> = emptyList(),
    val activeDeliveries: List<Delivery> = emptyList(),
    val error: String? = null,
    val username: String = "",
)

@HiltViewModel
class RiderJobsViewModel @Inject constructor(
    private val deliveryRepository: IDeliveryRepository,
    private val prefs: PreferencesManager,
) : ViewModel() {

    private val _state = MutableStateFlow(RiderJobsState())
    val state: StateFlow<RiderJobsState> = _state

    init {
        loadJobs()
    }

    fun loadJobs() {
        viewModelScope.launch {
            val username = prefs.username.first()
            _state.value = _state.value.copy(
                isLoading = true,
                username = username,
            )

            val availableResult = deliveryRepository.getAvailableDeliveries()
            val activeResult = deliveryRepository.getActiveDeliveries()

            val available = when (availableResult) {
                is NetworkResult.Success -> availableResult.data
                else -> emptyList()
            }
            val active = when (activeResult) {
                is NetworkResult.Success -> activeResult.data
                else -> emptyList()
            }

            _state.value = _state.value.copy(
                isLoading = false,
                availableDeliveries = available,
                activeDeliveries = active,
            )
        }
    }

    fun acceptDelivery(id: Int) {
        viewModelScope.launch {
            when (val result = deliveryRepository.acceptDelivery(id)) {
                is NetworkResult.Success -> loadJobs()
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(error = result.message)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun updateDeliveryStatus(id: Int, status: String) {
        viewModelScope.launch {
            when (val result = deliveryRepository.updateDeliveryStatus(id, status)) {
                is NetworkResult.Success -> loadJobs()
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(error = result.message)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}