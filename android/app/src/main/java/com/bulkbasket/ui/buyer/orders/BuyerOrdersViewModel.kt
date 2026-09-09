package com.bulkbasket.ui.buyer.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulkbasket.domain.model.Order
import com.bulkbasket.domain.repository.IOrderRepository
import com.bulkbasket.domain.repository.IReviewRepository
import com.bulkbasket.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BuyerOrdersState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val reviewedOrderIds: Set<String> = emptySet(),
    val error: String? = null,
    val ratingOrderId: String? = null,
    val isSubmittingReview: Boolean = false,
    val reviewError: String? = null,
)

@HiltViewModel
class BuyerOrdersViewModel @Inject constructor(
    private val orderRepository: IOrderRepository,
    private val reviewRepository: IReviewRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(BuyerOrdersState())
    val state: StateFlow<BuyerOrdersState> = _state

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = orderRepository.getOrders()) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        orders = result.data,
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

            when (val result = reviewRepository.getMyReviews()) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        reviewedOrderIds = result.data.map { it.orderId }.toSet(),
                    )
                }
                else -> {}
            }
        }
    }

    fun showRatingDialog(orderId: String) {
        _state.value = _state.value.copy(ratingOrderId = orderId, reviewError = null)
    }

    fun hideRatingDialog() {
        _state.value = _state.value.copy(ratingOrderId = null, reviewError = null)
    }

    fun submitReview(rating: Int, comment: String) {
        val orderId = _state.value.ratingOrderId ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isSubmittingReview = true, reviewError = null)
            when (val result = reviewRepository.createReview(orderId, rating, comment)) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isSubmittingReview = false,
                        ratingOrderId = null,
                        reviewedOrderIds = _state.value.reviewedOrderIds + orderId,
                    )
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(
                        isSubmittingReview = false,
                        reviewError = result.message,
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
