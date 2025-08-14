package com.example.pizzahutappfinal.pages

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pizzahutappfinal.viewmodel.ProfileUiState
import com.example.pizzahutappfinal.viewmodel.ProfileViewModel
import com.example.pizzahutappfinal.viewmodel.UpdateStatus
import com.example.pizzahutappfinal.R
import java.io.ByteArrayOutputStream

@Composable
fun EditProfilePage(
    modifier: Modifier,
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
) {

    val uiState by profileViewModel.userProfileState.collectAsState()
    val updateStatus by profileViewModel.updateStatus.collectAsState()
    val localImageBytes by profileViewModel.localImageBytes.collectAsState()
    val context = LocalContext.current

    var userName by remember { mutableStateOf("") }
    var userSurname by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }

    var saveChangesTrigger by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileViewModel.setLocalImageUriAndCompress(context, it)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            // Convierte el Bitmap de la cámara en un ByteArray y lo pasa al ViewModel
            val stream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray = stream.toByteArray()
            // Llama a una nueva función en el ViewModel para manejar el ByteArray
            profileViewModel.setLocalImageBytes(byteArray)
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is ProfileUiState.Success) {
            val user = (uiState as ProfileUiState.Success).user
            userName = user.nombre
            userSurname = user.apellidos
            userPhone = user.telefono
        }
    }

    LaunchedEffect(saveChangesTrigger) {
        if (saveChangesTrigger) {
            val updates = mutableMapOf<String, Any>()
            if (uiState is ProfileUiState.Success) {
                val currentUser = (uiState as ProfileUiState.Success).user
                if (userName != currentUser.nombre) updates["nombre"] = userName
                if (userSurname != currentUser.apellidos) updates["apellidos"] = userSurname
                if (userPhone.length == 9 && userPhone != currentUser.telefono) updates["telefono"] = userPhone
            }

            if (localImageBytes != null) {
                profileViewModel.uploadProfilePicture(updates)
            } else if (updates.isNotEmpty()) {
                profileViewModel.updateProfile(updates)
            } else {
                Toast.makeText(context, "No hay cambios para guardar", Toast.LENGTH_LONG).show()
            }
            saveChangesTrigger = false // Restablece el trigger
        }
    }

    LaunchedEffect(updateStatus) {
        when (updateStatus) {
            UpdateStatus.SUCCESS -> {
                Toast.makeText(context, "Perfil actualizado con exito", Toast.LENGTH_SHORT).show()
                profileViewModel.resetUpdateStatus()
                profileViewModel.resetLocalImageBytes()
                navController.navigate("profile_details") {
                    popUpTo("profile_details") { inclusive = true }
                    launchSingleTop = true
                }
            }
            UpdateStatus.ERROR -> {
                Toast.makeText(context, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
                profileViewModel.resetUpdateStatus()
            }
            else -> {}
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Seleccionar imagen de perfil") },
            text = { Text("Elige una opción para tu nueva foto.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        cameraLauncher.launch(null)
                        showDialog = false
                    }
                ) {
                    Text("Tomar foto")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        galleryLauncher.launch("image/*")
                        showDialog = false
                    }
                ) {
                    Text("Seleccionar de la galería")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Editar Perfil",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .clickable { showDialog = true }
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            // ✅ Priorizamos la imagen local seleccionada. Si no hay, usamos la de Firestore.
            val imageData = localImageBytes ?: (uiState as? ProfileUiState.Success)?.user?.profileImageBase64?.let { base64 ->
                try {
                    if (base64.isNotBlank()) Base64.decode(base64, Base64.DEFAULT) else null
                } catch (e: IllegalArgumentException) {
                    null
                }
            }

            if (imageData != null) {
                val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile_placeholder),
                    contentDescription = "Profile Picture Placeholder",
                    modifier = Modifier.size(75.dp),
                    tint = Color.White
                )
            }
            if (updateStatus == UpdateStatus.LOADING && localImageBytes != null) {
                CircularProgressIndicator(modifier = Modifier.size(40.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

    when (uiState) {
        is ProfileUiState.Loading -> {
            CircularProgressIndicator()
            Text(text = "Cargando datos")
        }

        is ProfileUiState.Error -> {
            Text(
                text = "Error al cargar el perfil: ${(uiState as ProfileUiState.Error).message}",
                color = MaterialTheme.colorScheme.error
            )
        }

        is ProfileUiState.Success -> {
            OutlinedTextField(
                value = userName,
                onValueChange = { newValue ->
                    if (newValue.all { it.isLetter() || it.isWhitespace() } || newValue.isBlank()) {
                        userName = newValue
                    }
                },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = userSurname,
                onValueChange = { newValue ->
                    if (newValue.all { it.isLetter() || it.isWhitespace() } || newValue.isBlank()) {
                        userSurname = newValue
                    }
                },
                label = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone TextField
            OutlinedTextField(
                value = userPhone,
                onValueChange = { newValue ->
                    if (newValue.length <= 9 && newValue.all { it.isDigit() }) {
                        userPhone = newValue
                    }
                },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (userName.isBlank() || userSurname.isBlank() || userPhone.isBlank()) {
                        Toast.makeText(context, "Por favor, complete todos los campos.", Toast.LENGTH_LONG).show()
                    } else if (userPhone.length != 9 && userPhone.isNotEmpty()) {
                        Toast.makeText(context, "El número de teléfono debe tener 9 dígitos", Toast.LENGTH_LONG).show()
                    } else {
                        saveChangesTrigger = true // Activa el LaunchedEffect para guardar los cambios
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC02128)),
                enabled = updateStatus != UpdateStatus.LOADING
            ) {
                if (updateStatus == UpdateStatus.LOADING) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(text = "Guardar cambios", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                enabled = updateStatus != UpdateStatus.LOADING
            ) {
                Text(
                    text = "Cancelar",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
            }
        }
    }

}

}