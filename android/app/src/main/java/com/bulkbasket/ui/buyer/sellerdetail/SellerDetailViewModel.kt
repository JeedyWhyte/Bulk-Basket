package com.bulkbasket.ui.buyer.sellerdetail

import androidx.lifecycle.SavedStateHandle
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

data class SellerDetailState(
    val isLoading: Boolean = false,
    val seller: Seller? = null,
    val error: String? = null,
)

@HiltViewModel
class SellerDetailViewModel @Inject constructor(
    private val productRepository: IProductRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val sellerId: Int = checkNotNull(
        savedStateHandle["sellerId"]
    )

    private val _state = MutableStateFlow(SellerDetailState())
    val state: StateFlow<SellerDetailState> = _state

    init {
        loadSeller()
    }

    fun loadSeller() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = productRepository.getSellerDetail(sellerId)) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        seller = result.data,
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
}