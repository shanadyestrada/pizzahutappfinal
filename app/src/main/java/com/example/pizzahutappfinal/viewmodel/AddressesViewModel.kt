package com.example.pizzahutappfinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzahutappfinal.model.DireccionModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddressesViewModel : ViewModel() {
    private val _addresses = MutableStateFlow<List<DireccionModel>>(emptyList())
    val addresses: StateFlow<List<DireccionModel>> = _addresses

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        fetchUserAddresses()
    }

    fun fetchUserAddresses() {
        _isLoading.value = true
        _addresses.value = emptyList()

        val userId = auth.currentUser?.uid
        if (userId == null) {
            _isLoading.value = false
            return
        }

        firestore.collection("usuarios").document(userId)
            .collection("direcciones")
            .get()
            .addOnSuccessListener { snapshot ->
                val addressList = snapshot.toObjects(DireccionModel::class.java)
                _addresses.value = addressList
                _isLoading.value = false
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                // Manejar el error
            }
    }
}