package com.bulkbasket.ui.buyer.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulkbasket.domain.model.Category
import com.bulkbasket.domain.model.Product
import com.bulkbasket.domain.repository.IProductRepository
import com.bulkbasket.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryState(
    val isLoadingCategories: Boolean = false,
    val isLoadingProducts: Boolean = false,
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val categoryProducts: List<Product> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val productRepository: IProductRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryState())
    val state: StateFlow<CategoryState> = _state

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoadingCategories = true,
                error = null,
            )
            when (val result = productRepository.getCategories()) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoadingCategories = false,
                        categories = result.data,
                    )
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoadingCategories = false,
                        error = result.message,
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun selectCategory(category: Category) {
        _state.value = _state.value.copy(
            selectedCategory = category,
            categoryProducts = emptyList(),
            isLoadingProducts = true,
        )
        loadProductsForCategory(category.slug)
    }

    fun clearSelectedCategory() {
        _state.value = _state.value.copy(
            selectedCategory = null,
            categoryProducts = emptyList(),
        )
    }

    private fun loadProductsForCategory(slug: String) {
        viewModelScope.launch {
            when (val result = productRepository.getProducts(
                category = slug,
            )) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoadingProducts = false,
                        categoryProducts = result.data,
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
