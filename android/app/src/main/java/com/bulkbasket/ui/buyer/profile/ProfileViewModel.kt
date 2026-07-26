package com.bulkbasket.ui.buyer.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulkbasket.domain.model.Address
import com.bulkbasket.domain.model.User
import com.bulkbasket.domain.repository.IAuthRepository
import com.bulkbasket.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val addresses: List<Address> = emptyList(),
    val error: String? = null,
    val isLoggedOut: Boolean = false,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val profileResult = authRepository.getProfile()
            val addressResult = authRepository.getAddresses()

            val user = when (profileResult) {
                is NetworkResult.Success -> profileResult.data
                else -> null
            }
            val addresses = when (addressResult) {
                is NetworkResult.Success -> addressResult.data
                else -> emptyList()
            }

            _state.value = _state.value.copy(
                isLoading = false,
                user = user,
                addresses = addresses,
                error = if (user == null) "Failed to load profile" else null,
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _state.value = _state.value.copy(isLoggedOut = true)
        }
    }
}