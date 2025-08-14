package com.example.pizzahutappfinal.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzahutappfinal.model.DireccionModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

        viewModelScope.launch {
            try{
                val snapshot = firestore.collection("usuarios").document(userId)
                    .collection("direcciones")
                    .get()
                    .await()

                val addressesList = snapshot.toObjects(DireccionModel::class.java)
                _addresses.value = addressesList
            }catch (e: Exception){
                Log.e("AddressesViewModel", "Error al cargar direcciones: ${e.message}")
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun addAddress(newAddress: DireccionModel) {
        val userId = auth.currentUser?.uid
        if (userId == null) return

        viewModelScope.launch {
            try{
                val docRef = firestore.collection("usuarios").document(userId)
                    .collection("direcciones").document()
                val addressWithId = newAddress.copy(direccionId = docRef.id)

                docRef.set(addressWithId).await()

                fetchUserAddresses()
            }catch(e: Exception){
                Log.e("AddressesViewModel", "Error al agregar dirección: ${e.message}")
            }
        }
    }

    fun deleteAddress(direccionId: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) return

        viewModelScope.launch {
            try {
                firestore.collection("usuarios").document(userId)
                    .collection("direcciones").document(direccionId)
                    .delete()
                    .await()

                // Refrescar la lista para que la UI se actualice
                fetchUserAddresses()
            } catch (e: Exception) {
                Log.e("AddressesViewModel", "Error al borrar dirección: ${e.message}")
            }
        }
    }
}