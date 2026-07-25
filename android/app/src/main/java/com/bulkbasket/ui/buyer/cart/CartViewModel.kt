package com.bulkbasket.ui.buyer.cart

import androidx.lifecycle.ViewModel
import com.bulkbasket.domain.model.CartItem
import com.bulkbasket.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class CartState(
    val items: List<CartItem> = emptyList(),
    val sellerId: Int? = null,
    val sellerName: String = "",
    val error: String? = null,
) {
    val subtotal: Double
        get() = items.sumOf { it.lineTotal }
    val deliveryFee: Double
        get() = if (items.isEmpty()) 0.0 else 500.0
    val total: Double
        get() = subtotal + deliveryFee
    val itemCount: Int
        get() = items.sumOf { it.quantity }
}

@HiltViewModel
class CartViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state

    fun addToCart(product: Product, sellerId: Int, sellerName: String) {
        val currentItems = _state.value.items.toMutableList()
        val currentSellerId = _state.value.sellerId

        // Only allow products from one seller at a time
        if (currentSellerId != null && currentSellerId != sellerId) {
            _state.value = _state.value.copy(
                error = "Your cart has items from ${_state.value.sellerName}. " +
                        "Clear cart to add from a different seller."
            )
            return
        }

        val existingIndex = currentItems.indexOfFirst {
            it.product.id == product.id
        }

        if (existingIndex >= 0) {
            val existing = currentItems[existingIndex]
            currentItems[existingIndex] = existing.copy(
                quantity = existing.quantity + 1
            )
        } else {
            currentItems.add(
                CartItem(
                    product = product,
                    quantity = 1,
                    sellerId = sellerId,
                    sellerName = sellerName,
                )
            )
        }

        _state.value = _state.value.copy(
            items = currentItems,
            sellerId = sellerId,
            sellerName = sellerName,
            error = null,
        )
    }

    fun increaseQuantity(productId: Int) {
        val currentItems = _state.value.items.toMutableList()
        val index = currentItems.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            val item = currentItems[index]
            currentItems[index] = item.copy(quantity = item.quantity + 1)
            _state.value = _state.value.copy(items = currentItems)
        }
    }

    fun decreaseQuantity(productId: Int) {
        val currentItems = _state.value.items.toMutableList()
        val index = currentItems.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            val item = currentItems[index]
            if (item.quantity <= 1) {
                currentItems.removeAt(index)
            } else {
                currentItems[index] = item.copy(quantity = item.quantity - 1)
            }
            _state.value = _state.value.copy(
                items = currentItems,
                sellerId = if (currentItems.isEmpty()) null
                else _state.value.sellerId,
                sellerName = if (currentItems.isEmpty()) ""
                else _state.value.sellerName,
            )
        }
    }

    fun removeItem(productId: Int) {
        val currentItems = _state.value.items
            .filter { it.product.id != productId }
        _state.value = _state.value.copy(
            items = currentItems,
            sellerId = if (currentItems.isEmpty()) null
            else _state.value.sellerId,
            sellerName = if (currentItems.isEmpty()) ""
            else _state.value.sellerName,
        )
    }

    fun clearCart() {
        _state.value = CartState()
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}