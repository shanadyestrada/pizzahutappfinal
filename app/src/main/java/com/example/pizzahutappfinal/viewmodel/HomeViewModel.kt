package com.example.pizzahutappfinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzahutappfinal.model.ProductModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    // MutableStateFlow para mantener el estado de la lista de productos
    private val _products = MutableStateFlow<List<ProductModel>>(emptyList())
    val products = _products.asStateFlow()

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            Firebase.firestore.collection("data").document("stock")
                .collection("productos")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val productList = querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(ProductModel::class.java)
                    }
                    _products.value = productList
                }
                .addOnFailureListener { e ->
                    // Manejar el error, por ejemplo, logearlo o mostrar un mensaje
                }
        }
    }
}