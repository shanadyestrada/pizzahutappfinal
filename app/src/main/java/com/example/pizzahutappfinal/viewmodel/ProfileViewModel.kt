package com.example.pizzahutappfinal.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
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
import java.io.ByteArrayOutputStream

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

    private val _localImageBytes = MutableStateFlow<ByteArray?>(null)
    val localImageBytes: StateFlow<ByteArray?> = _localImageBytes

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
                    val profileImageBase64 = documentSnapshot.getString("profileImageBase64") ?: ""
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
                        profileImageBase64 = profileImageBase64,
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

    fun setLocalImageUriAndCompress(context: Context, uri: Uri) {
        _updateStatus.value = UpdateStatus.LOADING
        viewModelScope.launch {
            try {
                // Se abre el InputStream desde la URI una sola vez.
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val originalBitmap = BitmapFactory.decodeStream(inputStream)

                    if (originalBitmap == null) {
                        _updateStatus.value = UpdateStatus.ERROR
                        Log.e("ProfileViewModel", "No se pudo decodificar el bitmap.")
                        return@launch
                    }

                    // Aquí se realiza la compresión
                    val outputStream = ByteArrayOutputStream()
                    var quality = 100
                    var byteArray: ByteArray
                    do {
                        outputStream.reset()
                        originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                        byteArray = outputStream.toByteArray()
                        quality -= 5
                    } while (byteArray.size > 1048576 && quality > 0) // Limite de 1MB

                    if (byteArray.size > 1048576) {
                        _updateStatus.value = UpdateStatus.ERROR
                        Log.e("ProfileViewModel", "La imagen sigue siendo demasiado grande después de la compresión.")
                        return@launch
                    }

                    _localImageBytes.value = byteArray
                    _updateStatus.value = UpdateStatus.IDLE
                }
            } catch (e: Exception) {
                _updateStatus.value = UpdateStatus.ERROR
                Log.e("ProfileViewModel", "Error al procesar la imagen: ${e.message}", e)
            }
        }
    }

    fun setLocalImageBytes(imageBytes: ByteArray) {
        _updateStatus.value = UpdateStatus.LOADING
        viewModelScope.launch {
            if (imageBytes.size > 1048576) {
                // Si la imagen de la cámara es demasiado grande, la comprimimos
                val originalBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                if (originalBitmap == null) {
                    _updateStatus.value = UpdateStatus.ERROR
                    Log.e("ProfileViewModel", "No se pudo decodificar el bitmap de la cámara.")
                    return@launch
                }

                val outputStream = ByteArrayOutputStream()
                var quality = 100
                var compressedBytes: ByteArray
                do {
                    outputStream.reset()
                    originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                    compressedBytes = outputStream.toByteArray()
                    quality -= 5
                } while (compressedBytes.size > 1048576 && quality > 0)

                if (compressedBytes.size > 1048576) {
                    _updateStatus.value = UpdateStatus.ERROR
                    Log.e("ProfileViewModel", "La imagen de la cámara sigue siendo demasiado grande después de la compresión.")
                    return@launch
                }
                _localImageBytes.value = compressedBytes
            } else {
                _localImageBytes.value = imageBytes
            }
            _updateStatus.value = UpdateStatus.IDLE
        }
    }

    fun uploadProfilePicture(updates: Map<String, Any>) {
        _updateStatus.value = UpdateStatus.LOADING
        viewModelScope.launch {
            val userId = Firebase.auth.currentUser?.uid
            val imageBytes = _localImageBytes.value

            if (userId == null || imageBytes == null) {
                _updateStatus.value = UpdateStatus.ERROR
                return@launch
            }

            try {
                val imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)

                // Combina los cambios de la imagen con los otros cambios
                val combinedUpdates = updates.toMutableMap()
                combinedUpdates["profileImageBase64"] = imageBase64

                updateProfile(combinedUpdates)
                _updateStatus.value = UpdateStatus.SUCCESS
            } catch (e: Exception) {
                _updateStatus.value = UpdateStatus.ERROR
                Log.e("ProfileViewModel", "Error al procesar la imagen: ${e.message}", e)
            }
        }
    }
    fun resetLocalImageBytes() {
        _localImageBytes.value = null
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