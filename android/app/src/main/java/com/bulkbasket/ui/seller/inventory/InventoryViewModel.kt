package com.bulkbasket.ui.seller.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulkbasket.data.remote.dto.ProductCreateRequest
import com.bulkbasket.domain.model.Product
import com.bulkbasket.domain.repository.IProductRepository
import com.bulkbasket.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InventoryState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null,
    val showAddDialog: Boolean = false,
    val editingProduct: Product? = null,
)

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val productRepository: IProductRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(InventoryState())
    val state: StateFlow<InventoryState> = _state

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = productRepository.getMyProducts()) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        products = result.data,
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

    fun showAddDialog() {
        _state.value = _state.value.copy(
            showAddDialog = true,
            editingProduct = null,
        )
    }

    fun showEditDialog(product: Product) {
        _state.value = _state.value.copy(
            showAddDialog = true,
            editingProduct = product,
        )
    }

    fun hideDialog() {
        _state.value = _state.value.copy(
            showAddDialog = false,
            editingProduct = null,
        )
    }

    fun createProduct(
        name: String,
        description: String,
        price: String,
        unit: String,
        minOrderQty: Int,
        stockQuantity: Int,
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val request = ProductCreateRequest(
                name = name,
                description = description,
                price = price,
                unit = unit,
                min_order_qty = minOrderQty,
                stock_quantity = stockQuantity,
                is_available = true,
            )
            when (val result = productRepository.createProduct(request)) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        showAddDialog = false,
                        successMessage = "Product added successfully.",
                    )
                    loadProducts()
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

    fun updateProduct(
        id: Int,
        name: String,
        description: String,
        price: String,
        unit: String,
        minOrderQty: Int,
        stockQuantity: Int,
        isAvailable: Boolean,
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val request = ProductCreateRequest(
                name = name,
                description = description,
                price = price,
                unit = unit,
                min_order_qty = minOrderQty,
                stock_quantity = stockQuantity,
                is_available = isAvailable,
            )
            when (val result = productRepository.updateProduct(id, request)) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        showAddDialog = false,
                        successMessage = "Product updated successfully.",
                    )
                    loadProducts()
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

    fun deleteProduct(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = productRepository.deleteProduct(id)) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Product deleted.",
                    )
                    loadProducts()
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

    fun clearMessages() {
        _state.value = _state.value.copy(
            error = null,
            successMessage = null,
        )
    }
}