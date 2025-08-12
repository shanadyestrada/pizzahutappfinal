package com.example.pizzahutappfinal.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzahutappfinal.model.CartItemModel
import com.example.pizzahutappfinal.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

enum class UpdateStatus{
    IDLE, LOADING, SUCCESS, ERROR
}

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: UserModel) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel  : ViewModel() {

    private val _userProfileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val userProfileState: StateFlow<ProfileUiState> = _userProfileState

    private val _updateStatus  = MutableStateFlow(UpdateStatus.IDLE)
    val updateStatus: StateFlow<UpdateStatus> = _updateStatus

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        _userProfileState.value = ProfileUiState.Loading

        viewModelScope.launch {
            val userId = Firebase.auth.currentUser?.uid
            if (userId == null) {
                _userProfileState.value = ProfileUiState.Error("Usuario no autenticado.")
                Log.e("ProfileViewModel", "User not authenticated (userId is null).")
                return@launch
            }

            val db = Firebase.firestore
            try {
                val documentSnapshot = db.collection("usuarios").document(userId).get().await()

                if (documentSnapshot.exists()) {
                    val nombre = documentSnapshot.getString("nombre") ?: ""
                    val apellidos = documentSnapshot.getString("apellidos") ?: ""
                    val fechaNacimiento = documentSnapshot.getString("fechaNacimiento") ?: ""
                    val telefono = documentSnapshot.getString("telefono") ?: ""
                    val email = documentSnapshot.getString("email") ?: ""
                    val firestoreUserId = documentSnapshot.getString("userId") ?: ""
                    val cartItemsData = documentSnapshot.get("cartItems") as? List<Map<String, Any>> ?: emptyList()

                    val cartItems = cartItemsData.map { itemMap ->
                        CartItemModel(
                            productoId = itemMap["productoId"] as? String ?: "",
                            variaciones = itemMap["variaciones"] as? String,
                            cantidad = itemMap["cantidad"] as? Long ?: 0,
                            adicionales = itemMap["adicionales"] as? List<String> ?: emptyList()
                        )
                    }

                    val user = UserModel(
                        nombre = nombre,
                        apellidos = apellidos,
                        fechaNacimiento = fechaNacimiento,
                        telefono = telefono,
                        email = email,
                        userId = firestoreUserId,
                        cartItems = cartItems
                    )
                    _userProfileState.value = ProfileUiState.Success(user)
                    Log.d("ProfileViewModel", "User data loaded successfully (manual): $user")
                } else {
                    _userProfileState.value = ProfileUiState.Error("No se encontraron datos del usuario en Firestore para el ID: $userId")
                    Log.e("ProfileViewModel", "Document for user ID $userId does not exist.")
                }
            } catch (e: Exception) {
                _userProfileState.value = ProfileUiState.Error("Error al cargar los datos: ${e.localizedMessage}")
                Log.e("ProfileViewModel", "Error getting user data: ${e.message}", e)
            }
        }
    }

    fun updateProfile(updates: Map<String, Any>){
        _updateStatus.value = UpdateStatus.LOADING
        viewModelScope.launch {
            val userId = Firebase.auth.currentUser?.uid
            if (userId == null){
                _updateStatus.value = UpdateStatus.ERROR
                Log.e("ProfileViewModel","User not authenticated for update")
                return@launch
            }
            val db = Firebase.firestore
            try {
                db.collection("usuarios").document(userId).update(updates).await()
                _updateStatus.value = UpdateStatus.SUCCESS
                Log.d("ProfileViewModel", "Profile updated succesfully $updates")
                loadUserProfile()
            } catch (e: Exception){
                _updateStatus.value = UpdateStatus.ERROR
                Log.e("ProfileViewModel", "Error udpating profile: ${e.message}", e)
            }

            fun resetUpdateStatus() {
                _updateStatus.value = UpdateStatus.IDLE
            }
        }
    }

    fun resetUpdateStatus() {
        _updateStatus.value = UpdateStatus.IDLE
    }
}