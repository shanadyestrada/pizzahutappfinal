package com.example.pizzahutappfinal.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pizzahutappfinal.AppUtil
import com.example.pizzahutappfinal.model.OrderModel
import com.example.pizzahutappfinal.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore

class InvoiceViewModel(private val orderId: String) : ViewModel() {
    private val _order = MutableLiveData<OrderModel?>()
    val order: LiveData<OrderModel?> = _order

    private val _userProfile = MutableLiveData<UserModel?>()
    val userProfile: LiveData<UserModel?> = _userProfile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _orderTotal = MutableLiveData<Double>()
    val orderTotal: LiveData<Double> = _orderTotal

    init {
        loadOrder()
    }

    private fun loadOrder() {
        _isLoading.value = true
        FirebaseFirestore.getInstance().collection("orders").document(orderId).get()
            .addOnSuccessListener { documentSnapshot ->
                val orderModel = documentSnapshot.toObject(OrderModel::class.java)
                _order.value = orderModel
                if (orderModel != null) {
                    // ✅ Usamos tu función calculateTotal y pasamos un callback para actualizar el LiveData
                    AppUtil.calculateTotal(cartItems = orderModel.cartItems) { total ->
                        _orderTotal.value = total
                    }

                    if (orderModel.userId.isNotEmpty()) {
                        loadUserProfile(orderModel.userId)
                    } else {
                        _isLoading.value = false
                    }
                } else {
                    _isLoading.value = false
                }
            }
            .addOnFailureListener {
                _order.value = null
                _isLoading.value = false
            }
    }

    private fun loadUserProfile(userId: String) {
        FirebaseFirestore.getInstance().collection("users").document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                val userModel = documentSnapshot.toObject(UserModel::class.java)
                _userProfile.value = userModel
                _isLoading.value = false
            }
            .addOnFailureListener {
                _userProfile.value = null
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