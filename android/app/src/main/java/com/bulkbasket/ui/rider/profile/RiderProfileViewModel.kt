package com.bulkbasket.ui.rider.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulkbasket.domain.model.RiderProfile
import com.bulkbasket.domain.repository.IAuthRepository
import com.bulkbasket.domain.repository.IDeliveryRepository
import com.bulkbasket.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RiderProfileState(
    val isLoading: Boolean = false,
    val profile: RiderProfile? = null,
    val hasProfile: Boolean = false,
    val isAvailable: Boolean = true,
    val error: String? = null,
    val successMessage: String? = null,
    val isLoggedOut: Boolean = false,
)

@HiltViewModel
class RiderProfileViewModel @Inject constructor(
    private val deliveryRepository: IDeliveryRepository,
    private val authRepository: IAuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RiderProfileState())
    val state: StateFlow<RiderProfileState> = _state

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = deliveryRepository.getRiderProfile()) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        profile = result.data,
                        hasProfile = true,
                        isAvailable = result.data.isAvailable,
                    )
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        hasProfile = false,
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun setupProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = deliveryRepository.createRiderProfile(
                isAvailable = true
            )) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        profile = result.data,
                        hasProfile = true,
                        isAvailable = true,
                        successMessage = "Profile created successfully.",
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

    fun toggleAvailability() {
        val newAvailability = !_state.value.isAvailable
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = deliveryRepository.updateRiderProfile(
                isAvailable = newAvailability
            )) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        profile = result.data,
                        isAvailable = newAvailability,
                        successMessage = if (newAvailability)
                            "You are now available for deliveries."
                        else
                            "You are now offline.",
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