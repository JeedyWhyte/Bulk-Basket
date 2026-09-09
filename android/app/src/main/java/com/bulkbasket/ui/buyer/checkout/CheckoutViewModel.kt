package com.bulkbasket.ui.buyer.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulkbasket.data.remote.dto.OrderCreateRequest
import com.bulkbasket.data.remote.dto.OrderItemRequest
import com.bulkbasket.domain.model.Address
import com.bulkbasket.domain.repository.IAuthRepository
import com.bulkbasket.domain.repository.IOrderRepository
import com.bulkbasket.domain.repository.IPaymentRepository
import com.bulkbasket.ui.buyer.cart.CartViewModel
import com.bulkbasket.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val PAYMENT_METHOD_CASH = "cash_on_delivery"
const val PAYMENT_METHOD_DEMO_CARD = "demo_card"

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
    val paymentMethod: String = PAYMENT_METHOD_CASH,
    val showCardDialog: Boolean = false,
    val cardNumber: String = "",
    val cardExpiryMonth: Int? = null,
    val cardExpiryYear: Int? = null,
    val cardCvv: String = "",
    val paymentReference: String? = null,
    val paymentDeclineMessage: String? = null,
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: IOrderRepository,
    private val authRepository: IAuthRepository,
    private val paymentRepository: IPaymentRepository,
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

    fun selectPaymentMethod(method: String) {
        _state.value = _state.value.copy(paymentMethod = method)
        if (method == PAYMENT_METHOD_DEMO_CARD && _state.value.cardNumber.isBlank()) {
            showCardDialog()
        }
    }

    fun showCardDialog() {
        _state.value = _state.value.copy(showCardDialog = true)
    }

    fun hideCardDialog() {
        _state.value = _state.value.copy(showCardDialog = false)
    }

    fun saveCardDetails(number: String, month: Int, year: Int, cvv: String) {
        _state.value = _state.value.copy(
            cardNumber = number,
            cardExpiryMonth = month,
            cardExpiryYear = year,
            cardCvv = cvv,
            showCardDialog = false,
        )
    }

    fun acknowledgeDecline() {
        _state.value = _state.value.copy(
            paymentDeclineMessage = null,
            orderPlaced = true,
        )
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
        if (_state.value.paymentMethod == PAYMENT_METHOD_DEMO_CARD &&
            _state.value.cardNumber.isBlank()
        ) {
            _state.value = _state.value.copy(
                error = "Please enter your demo card details."
            )
            showCardDialog()
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
                    val order = result.data

                    if (_state.value.paymentMethod == PAYMENT_METHOD_DEMO_CARD) {
                        when (val chargeResult = paymentRepository.chargeOrder(
                            orderId = order.id,
                            method = PAYMENT_METHOD_DEMO_CARD,
                            cardNumber = _state.value.cardNumber,
                            expiryMonth = _state.value.cardExpiryMonth,
                            expiryYear = _state.value.cardExpiryYear,
                            cvv = _state.value.cardCvv,
                        )) {
                            is NetworkResult.Success -> {
                                _state.value = _state.value.copy(
                                    isPlacingOrder = false,
                                    orderPlaced = true,
                                    orderId = order.id,
                                    paymentReference = chargeResult.data.reference,
                                )
                            }
                            is NetworkResult.Error -> {
                                _state.value = _state.value.copy(
                                    isPlacingOrder = false,
                                    orderId = order.id,
                                    paymentDeclineMessage = chargeResult.message,
                                )
                            }
                            is NetworkResult.Loading -> {}
                        }
                    } else {
                        _state.value = _state.value.copy(
                            isPlacingOrder = false,
                            orderPlaced = true,
                            orderId = order.id,
                        )
                    }
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
