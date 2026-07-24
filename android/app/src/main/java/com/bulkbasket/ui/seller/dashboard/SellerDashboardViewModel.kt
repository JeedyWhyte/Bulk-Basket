package com.bulkbasket.ui.seller.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulkbasket.domain.model.Order
import com.bulkbasket.domain.repository.IOrderRepository
import com.bulkbasket.utils.NetworkResult
import com.bulkbasket.utils.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SellerDashboardState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val error: String? = null,
    val username: String = "",
    val pendingCount: Int = 0,
    val confirmedCount: Int = 0,
    val completedCount: Int = 0,
)

@HiltViewModel
class SellerDashboardViewModel @Inject constructor(
    private val orderRepository: IOrderRepository,
    private val prefs: PreferencesManager,
) : ViewModel() {

    private val _state = MutableStateFlow(SellerDashboardState())
    val state: StateFlow<SellerDashboardState> = _state

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            val username = prefs.username.first()
            _state.value = _state.value.copy(
                isLoading = true,
                username = username,
            )
            when (val result = orderRepository.getSellerOrders()) {
                is NetworkResult.Success -> {
                    val orders = result.data
                    _state.value = _state.value.copy(
                        isLoading = false,
                        orders = orders,
                        pendingCount = orders.count {
                            it.status == "pending"
                        },
                        confirmedCount = orders.count {
                            it.status in listOf("confirmed", "preparing", "ready")
                        },
                        completedCount = orders.count {
                            it.status == "delivered"
                        },
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

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            when (val result = orderRepository.updateOrderStatus(orderId, newStatus)) {
                is NetworkResult.Success -> {
                    loadDashboard()
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(error = result.message)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}