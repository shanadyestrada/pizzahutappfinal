package com.example.pizzahutappfinal.components

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pizzahutappfinal.model.OrderModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class InvoiceViewModel(private val orderId: String) : ViewModel() {
    private val _order = MutableLiveData<OrderModel?>()
    val order: LiveData<OrderModel?> = _order

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadOrder()
    }

    private fun loadOrder() {
        _isLoading.value = true
        FirebaseFirestore.getInstance().collection("orders").document(orderId).get()
            .addOnSuccessListener { documentSnapshot ->
                val orderModel = documentSnapshot.toObject(OrderModel::class.java)
                _order.value = orderModel
                _isLoading.value = false
            }
            .addOnFailureListener {
                _order.value = null
                _isLoading.value = false
            }
    }

    class InvoiceViewModelFactory(private val orderId: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InvoiceViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return InvoiceViewModel(orderId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}