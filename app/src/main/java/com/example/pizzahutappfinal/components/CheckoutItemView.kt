package com.example.pizzahutappfinal.components

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzahutappfinal.AppUtil
import com.example.pizzahutappfinal.model.CartItemModel
import com.example.pizzahutappfinal.model.DireccionModel
import com.example.pizzahutappfinal.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class CheckoutViewModel : ViewModel() {
    private val _cartItems = MutableLiveData<List<CartItemModel>>()
    val cartItems: LiveData<List<CartItemModel>> = _cartItems

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double> = _totalPrice

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userProfile = MutableLiveData<UserModel?>()
    val userProfile: LiveData<UserModel?> = _userProfile

    private val _direcciones = MutableLiveData<List<DireccionModel>>()
    val direcciones: LiveData<List<DireccionModel>> = _direcciones

    init {
        loadCartItems()
        loadUserProfile()
        loadUserAddresses()
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            _isLoading.value = true
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

    private fun loadUserProfile() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    _userProfile.value = documentSnapshot.toObject(UserModel::class.java)
                }
                .addOnFailureListener {
                    _userProfile.value = null
                }
        }
    }

    private fun loadUserAddresses() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("usuarios").document(userId)
                .collection("direcciones")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val direccionesList = querySnapshot.toObjects(DireccionModel::class.java)
                    _direcciones.value = direccionesList
                }
                .addOnFailureListener {
                    _direcciones.value = emptyList()
                }
        }
    }
}