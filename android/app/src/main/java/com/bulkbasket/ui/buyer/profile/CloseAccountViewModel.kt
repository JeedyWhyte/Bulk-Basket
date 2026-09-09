package com.bulkbasket.ui.buyer.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulkbasket.domain.repository.IAuthRepository
import com.bulkbasket.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CloseAccountState(
    val showConfirmDialog: Boolean = false,
    val isClosing: Boolean = false,
    val error: String? = null,
    val closed: Boolean = false,
)

@HiltViewModel
class CloseAccountViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CloseAccountState())
    val state: StateFlow<CloseAccountState> = _state

    fun showConfirmDialog() {
        _state.value = _state.value.copy(showConfirmDialog = true)
    }

    fun hideConfirmDialog() {
        _state.value = _state.value.copy(showConfirmDialog = false)
    }

    fun closeAccount() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isClosing = true, error = null)
            when (val result = authRepository.closeAccount()) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isClosing = false,
                        showConfirmDialog = false,
                        closed = true,
                    )
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(
                        isClosing = false,
                        error = result.message,
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
