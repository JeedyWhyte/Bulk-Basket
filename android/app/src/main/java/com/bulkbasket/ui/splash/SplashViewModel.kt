package com.bulkbasket.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulkbasket.utils.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SplashDestination {
    Loading, Login, Buyer, Seller, Rider
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prefs: PreferencesManager,
) : ViewModel() {

    private val _destination = MutableStateFlow(SplashDestination.Loading)
    val destination: StateFlow<SplashDestination> = _destination

    init {
        viewModelScope.launch {
            delay(1500L)
            combine(
                prefs.accessToken,
                prefs.userRole,
            ) { token, role ->
                Pair(token, role)
            }.collect { (token, role) ->
                _destination.value = if (token.isEmpty()) {
                    SplashDestination.Login
                } else {
                    when (role) {
                        "buyer"  -> SplashDestination.Buyer
                        "seller" -> SplashDestination.Seller
                        "rider"  -> SplashDestination.Rider
                        else     -> SplashDestination.Login
                    }
                }
            }
        }
    }
}