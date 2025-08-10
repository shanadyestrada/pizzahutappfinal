package com.example.pizzahutappfinal.components

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzahutappfinal.AppUtil
import com.example.pizzahutappfinal.model.CartItemModel
import kotlinx.coroutines.launch

class CheckoutViewModel : ViewModel() {
    private val _cartItems = MutableLiveData<List<CartItemModel>>()
    val cartItems: LiveData<List<CartItemModel>> = _cartItems

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double> = _totalPrice

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadCartItems()
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            _isLoading.value = true
            // Cargar los ítems del carrito desde Firestore
            AppUtil.getCartItems { loadedItems ->
                _cartItems.value = loadedItems
                calculateTotal()
                _isLoading.value = false
            }
        }
    }

    private fun calculateTotal() {
        val currentCartItems = _cartItems.value ?: emptyList()
        // Llamar a la función calculateTotal de AppUtil
        AppUtil.calculateTotal(currentCartItems) { total ->
            _totalPrice.value = total
        }
    }
}