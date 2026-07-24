package com.bulkbasket.ui.buyer.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulkbasket.domain.model.Product
import com.bulkbasket.domain.model.Seller
import com.bulkbasket.domain.repository.IProductRepository
import com.bulkbasket.utils.NetworkResult
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
    val userLat: Double = 6.5244,
    val userLng: Double = 3.3792,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: IProductRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    init {
        loadHomeData()
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
            when (val result = productRepository.getProducts()) {
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