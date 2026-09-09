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
    val isSavingProfile: Boolean = false,
    val saveProfileError: String? = null,
    val profileSaved: Boolean = false,
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

    fun updateProfile(username: String, email: String, phoneNumber: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isSavingProfile = true,
                saveProfileError = null,
                profileSaved = false,
            )
            when (val result = authRepository.updateProfile(username, email, phoneNumber)) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isSavingProfile = false,
                        user = result.data,
                        profileSaved = true,
                    )
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(
                        isSavingProfile = false,
                        saveProfileError = result.message,
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun clearProfileSaved() {
        _state.value = _state.value.copy(profileSaved = false)
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _state.value = _state.value.copy(isLoggedOut = true)
        }
    }
}
