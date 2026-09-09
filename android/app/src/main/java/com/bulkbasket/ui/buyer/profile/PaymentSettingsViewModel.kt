package com.bulkbasket.ui.buyer.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulkbasket.domain.model.PaymentMethod
import com.bulkbasket.domain.repository.IPaymentRepository
import com.bulkbasket.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaymentSettingsState(
    val isLoading: Boolean = false,
    val methods: List<PaymentMethod> = emptyList(),
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val isSaving: Boolean = false,
)

@HiltViewModel
class PaymentSettingsViewModel @Inject constructor(
    private val paymentRepository: IPaymentRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentSettingsState())
    val state: StateFlow<PaymentSettingsState> = _state

    init {
        loadMethods()
    }

    fun loadMethods() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = paymentRepository.getPaymentMethods()) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        methods = result.data,
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

    fun showAddDialog() {
        _state.value = _state.value.copy(showAddDialog = true)
    }

    fun hideAddDialog() {
        _state.value = _state.value.copy(showAddDialog = false)
    }

    fun addMethod(brand: String, last4: String, month: Int, year: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null)
            when (val result = paymentRepository.addPaymentMethod(brand, last4, month, year)) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        showAddDialog = false,
                        methods = _state.value.methods + result.data,
                    )
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        error = result.message,
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun removeMethod(id: Int) {
        viewModelScope.launch {
            when (val result = paymentRepository.deletePaymentMethod(id)) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        methods = _state.value.methods.filterNot { it.id == id },
                    )
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(error = result.message)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
