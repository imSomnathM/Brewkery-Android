package com.example.brewkery.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brewkery.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface MenuUiState {
    data object Loading : MenuUiState
    data class Success(val data: MenuResponse) : MenuUiState
    data class Error(val message: String) : MenuUiState
}

data class ActiveOrder(val ticketId: String, val itemCount: Int)

/** A short message shown in the top toast. The id makes repeated identical messages show again. */
data class ToastMsg(val id: Long, val text: String)

class BrewViewModel : ViewModel() {

    private val _menu = MutableStateFlow<MenuUiState>(MenuUiState.Loading)
    val menu: StateFlow<MenuUiState> = _menu.asStateFlow()

    private val _cart = MutableStateFlow<List<CartLine>>(emptyList())
    val cart: StateFlow<List<CartLine>> = _cart.asStateFlow()

    private val _order = MutableStateFlow<ActiveOrder?>(null)
    val order: StateFlow<ActiveOrder?> = _order.asStateFlow()

    private val _toast = MutableStateFlow<ToastMsg?>(null)
    val toast: StateFlow<ToastMsg?> = _toast.asStateFlow()
    private var toastCounter = 0L

    init { load() }

    fun load() {
        _menu.value = MenuUiState.Loading
        viewModelScope.launch {
            _menu.value = try {
                MenuUiState.Success(ApiClient.api.getMenu())
            } catch (e: Exception) {
                MenuUiState.Error("Please check your internet connection and try again.")
            }
        }
    }

    private fun showToast(text: String) {
        _toast.value = ToastMsg(++toastCounter, text)
    }

    fun dismissToast(id: Long) {
        if (_toast.value?.id == id) _toast.value = null
    }

    fun addToCart(item: MenuItem, size: SizeOption, milk: MilkOption?, sugar: String, qty: Int) {
        val key = "${item.id}|${size.id}|${milk?.id}|$sugar"
        _cart.update { lines ->
            if (lines.any { it.key == key }) {
                lines.map { if (it.key == key) it.copy(quantity = it.quantity + qty) else it }
            } else {
                lines + CartLine(key, item, size, milk, sugar, qty)
            }
        }
        showToast("Added ${qty}× ${item.name} to Cart")
    }

    fun changeQty(key: String, delta: Int) {
        _cart.update { lines ->
            lines.map { if (it.key == key) it.copy(quantity = it.quantity + delta) else it }
                .filter { it.quantity > 0 }
        }
    }

    fun clearCart() {
        if (_cart.value.isEmpty()) return
        _cart.value = emptyList()
        showToast("Cart cleared")
    }

    fun placeOrder() {
        val lines = _cart.value
        if (lines.isEmpty()) return
        _order.value = ActiveOrder("BK-${(10000..99999).random()}", lines.sumOf { it.quantity })
        _cart.value = emptyList()
        showToast("🎉 Order Placed! Cart cleared.")
    }
}
