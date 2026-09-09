package com.bulkbasket.ui.buyer.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulkbasket.utils.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class NotificationPreferencesState(
    val orderUpdates: Boolean = true,
    val promotions: Boolean = true,
    val newArrivals: Boolean = true,
)

@HiltViewModel
class NotificationPreferencesViewModel @Inject constructor(
    private val prefs: PreferencesManager,
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationPreferencesState())
    val state: StateFlow<NotificationPreferencesState> = _state

    init {
        viewModelScope.launch {
            prefs.notifyOrderUpdates.collect {
                _state.value = _state.value.copy(orderUpdates = it)
            }
        }
        viewModelScope.launch {
            prefs.notifyPromotions.collect {
                _state.value = _state.value.copy(promotions = it)
            }
        }
        viewModelScope.launch {
            prefs.notifyNewArrivals.collect {
                _state.value = _state.value.copy(newArrivals = it)
            }
        }
    }

    fun setOrderUpdates(enabled: Boolean) {
        viewModelScope.launch { prefs.setNotifyOrderUpdates(enabled) }
    }

    fun setPromotions(enabled: Boolean) {
        viewModelScope.launch { prefs.setNotifyPromotions(enabled) }
    }

    fun setNewArrivals(enabled: Boolean) {
        viewModelScope.launch { prefs.setNotifyNewArrivals(enabled) }
    }
}
