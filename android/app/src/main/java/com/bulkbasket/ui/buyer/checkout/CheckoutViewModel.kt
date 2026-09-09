package com.bulkbasket.ui.buyer.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulkbasket.data.remote.dto.OrderCreateRequest
import com.bulkbasket.data.remote.dto.OrderItemRequest
import com.bulkbasket.domain.model.Address
import com.bulkbasket.domain.repository.IAuthRepository
import com.bulkbasket.domain.repository.IOrderRepository
import com.bulkbasket.ui.buyer.cart.CartViewModel
import com.bulkbasket.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutState(
    val isLoadingAddresses: Boolean = false,
    val isPlacingOrder: Boolean = false,
    val addresses: List<Address> = emptyList(),
    val selectedAddressId: Int? = null,
    val error: String? = null,
    val orderPlaced: Boolean = false,
    val orderId: String? = null,
    val showAddAddressDialog: Boolean = false,
    val isSavingAddress: Boolean = false,
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: IOrderRepository,
    private val authRepository: IAuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutState())
    val state: StateFlow<CheckoutState> = _state

    init {
        loadAddresses()
    }

    fun loadAddresses() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingAddresses = true)
            when (val result = authRepository.getAddresses()) {
                is NetworkResult.Success -> {
                    val addresses = result.data
                    _state.value = _state.value.copy(
                        isLoadingAddresses = false,
                        addresses = addresses,
                        selectedAddressId = addresses.firstOrNull {
                            it.isDefault
                        }?.id ?: addresses.firstOrNull()?.id,
                    )
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoadingAddresses = false,
                        error = result.message,
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun selectAddress(id: Int) {
        _state.value = _state.value.copy(selectedAddressId = id)
    }

    fun showAddAddressDialog() {
        _state.value = _state.value.copy(showAddAddressDialog = true)
    }

    fun hideAddAddressDialog() {
        _state.value = _state.value.copy(showAddAddressDialog = false)
    }

    fun createAddress(
        label: String,
        street: String,
        city: String,
        state: String,
    ) {
        if (label.isBlank() || street.isBlank() || city.isBlank() || state.isBlank()) {
            _state.value = _state.value.copy(
                error = "Please fill in every address field."
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isSavingAddress = true, error = null)
            val makeDefault = _state.value.addresses.isEmpty()
            when (val result = authRepository.createAddress(
                label = label,
                street = street,
                city = city,
                state = state,
                isDefault = makeDefault,
            )) {
                is NetworkResult.Success -> {
                    val updated = _state.value.addresses + result.data
                    _state.value = _state.value.copy(
                        isSavingAddress = false,
                        showAddAddressDialog = false,
                        addresses = updated,
                        selectedAddressId = result.data.id,
                    )
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(
                        isSavingAddress = false,
                        error = result.message,
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun placeOrder(cartViewModel: CartViewModel) {
        val cartState = cartViewModel.state.value
        val selectedAddressId = _state.value.selectedAddressId
        val sellerId = cartState.sellerId

        if (selectedAddressId == null) {
            _state.value = _state.value.copy(
                error = "Please select a delivery address."
            )
            return
        }
        if (sellerId == null || cartState.items.isEmpty()) {
            _state.value = _state.value.copy(
                error = "Your cart is empty."
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isPlacingOrder = true,
                error = null,
            )

            val request = OrderCreateRequest(
                seller_id = sellerId,
                delivery_address_id = selectedAddressId,
                items = cartState.items.map {
                    OrderItemRequest(
                        product_id = it.product.id,
                        quantity = it.quantity,
                    )
                },
            )

            when (val result = orderRepository.createOrder(request)) {
                is NetworkResult.Success -> {
                    cartViewModel.clearCart()
                    _state.value = _state.value.copy(
                        isPlacingOrder = false,
                        orderPlaced = true,
                        orderId = result.data.id,
                    )
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(
                        isPlacingOrder = false,
                        error = result.message,
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}