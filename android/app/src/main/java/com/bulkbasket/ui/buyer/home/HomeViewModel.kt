package com.bulkbasket.ui.buyer.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulkbasket.domain.model.Product
import com.bulkbasket.domain.model.Seller
import com.bulkbasket.domain.repository.IAuthRepository
import com.bulkbasket.domain.repository.IProductRepository
import com.bulkbasket.utils.NetworkResult
import com.bulkbasket.utils.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val isLoadingSellers: Boolean = false,
    val isLoadingProducts: Boolean = false,
    val nearbySellers: List<Seller> = emptyList(),
    val featuredProducts: List<Product> = emptyList(),
    val error: String? = null,
    val username: String = "",
    // Fallback: Lagos. Replaced by the user's default address coordinates
    // when they have one with a saved latitude/longitude.
    val userLat: Double = 6.6018,
    val userLng: Double = 3.3515,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: IProductRepository,
    private val authRepository: IAuthRepository,
    private val prefs: PreferencesManager,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    init {
        loadUsername()
        viewModelScope.launch {
            resolveUserLocation()
            loadHomeData()
        }
    }

    /**
     * Uses the buyer's default saved address as the search origin when it
     * has coordinates; otherwise keeps the fallback location.
     */
    private suspend fun resolveUserLocation() {
        val result = authRepository.getAddresses()
        if (result is NetworkResult.Success) {
            val address = result.data.firstOrNull { it.isDefault }
                ?: result.data.firstOrNull()
            val lat = address?.latitude?.toDoubleOrNull()
            val lng = address?.longitude?.toDoubleOrNull()
            if (lat != null && lng != null) {
                _state.value = _state.value.copy(userLat = lat, userLng = lng)
            }
        }
    }

    private fun loadUsername() {
        viewModelScope.launch {
            prefs.username.collect { name ->
                _state.value = _state.value.copy(username = name)
            }
        }
    }

    fun loadHomeData() {
        loadNearbySellers()
        loadFeaturedProducts()
    }

    private fun loadNearbySellers() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingSellers = true)
            when (val result = productRepository.getNearbySellers(
                lat = _state.value.userLat,
                lng = _state.value.userLng,
                radius = 50.0,
            )) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoadingSellers = false,
                        nearbySellers = result.data,
                    )
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoadingSellers = false,
                        error = result.message,
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun loadFeaturedProducts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingProducts = true)
            when (val result = productRepository.getProducts(
                page = 1,
            )) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoadingProducts = false,
                        featuredProducts = result.data,
                    )
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoadingProducts = false,
                        error = result.message,
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
