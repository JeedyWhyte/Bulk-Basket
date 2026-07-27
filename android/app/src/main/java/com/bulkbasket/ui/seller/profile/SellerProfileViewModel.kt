package com.bulkbasket.ui.seller.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulkbasket.data.remote.dto.SellerProfileCreateRequest
import com.bulkbasket.domain.model.Seller
import com.bulkbasket.domain.repository.IAuthRepository
import com.bulkbasket.domain.repository.IProductRepository
import com.bulkbasket.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.bulkbasket.ui.theme.ThemeMode
import com.bulkbasket.ui.theme.ThemeViewModel

data class SellerProfileState(
    val isLoading: Boolean = false,
    val seller: Seller? = null,
    val hasProfile: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val isLoggedOut: Boolean = false,
    val showEditDialog: Boolean = false,
)

@HiltViewModel
class SellerProfileViewModel @Inject constructor(
    private val productRepository: IProductRepository,
    private val authRepository: IAuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SellerProfileState())
    val state: StateFlow<SellerProfileState> = _state

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = productRepository.getMySellerProfile()) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        seller = result.data,
                        hasProfile = true,
                    )
                }
                is NetworkResult.Error -> {
                    // 404 means no profile yet
                    _state.value = _state.value.copy(
                        isLoading = false,
                        hasProfile = false,
                        seller = null,
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun showEditDialog() {
        _state.value = _state.value.copy(showEditDialog = true)
    }

    fun hideEditDialog() {
        _state.value = _state.value.copy(showEditDialog = false)
    }

    fun saveProfile(
        businessName: String,
        marketName: String,
        description: String,
        openingTime: String,
        closingTime: String,
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val request = SellerProfileCreateRequest(
                business_name = businessName,
                market_name = marketName,
                description = description,
                latitude = null,
                longitude = null,
                opening_time = openingTime.ifBlank { null },
                closing_time = closingTime.ifBlank { null },
            )
            val result = if (_state.value.hasProfile) {
                productRepository.updateSellerProfile(request)
            } else {
                productRepository.createSellerProfile(request)
            }
            when (result) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        seller = result.data,
                        hasProfile = true,
                        showEditDialog = false,
                        successMessage = "Profile saved successfully.",
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

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _state.value = _state.value.copy(isLoggedOut = true)
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(
            error = null,
            successMessage = null,
        )
    }
}
