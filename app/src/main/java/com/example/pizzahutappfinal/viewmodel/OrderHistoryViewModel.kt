package com.example.pizzahutappfinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzahutappfinal.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class OrderHistoryViewModel : ViewModel() {

    // MutableStateFlow para mantener el estado de la UI
    private val _orders = MutableStateFlow<List<OrderModel>>(emptyList())
    val orders: StateFlow<List<OrderModel>> = _orders

    // MutableStateFlow para manejar el estado de carga
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // MutableStateFlow para manejar los errores
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        // Llama a la función para obtener los pedidos al inicializar el ViewModel
        fetchUserOrders()
    }

    private fun fetchUserOrders() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            _error.value = "Usuario no autenticado."
            _isLoading.value = false
            return
        }

        // Obtiene una instancia de Firestore
        val firestore = FirebaseFirestore.getInstance()

        // Escucha en tiempo real la colección "orders"
        firestore.collection("orders")
            // Filtra los documentos donde el campo "userId" sea igual al ID del usuario
            .whereEqualTo("userId", userId)
            // Agrega un listener para recibir actualizaciones en tiempo real
            .addSnapshotListener { snapshot, e ->
                _isLoading.value = false // Detiene el indicador de carga
                if (e != null) {
                    _error.value = "Error al obtener los pedidos: ${e.message}"
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val ordersList = snapshot.documents.mapNotNull { document ->
                        document.toObject(OrderModel::class.java)
                    }
                    _orders.value = ordersList
                    _error.value = null // Limpia cualquier error anterior
                } else {
                    _orders.value = emptyList() // No hay pedidos
                }
            }
    }
}
