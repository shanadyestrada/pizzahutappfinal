package com.example.pizzahutappfinal.pages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pizzahutappfinal.viewmodel.ProfileUiState
import com.example.pizzahutappfinal.viewmodel.ProfileViewModel
import com.example.pizzahutappfinal.viewmodel.UpdateStatus

@Composable
fun EditProfilePage(
    modifier: Modifier,
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
){

    val uiState by profileViewModel.userProfileState.collectAsState()
    val updateStatus by profileViewModel.updateStatus.collectAsState()
    val context = LocalContext.current

    var userName by remember { mutableStateOf("") }
    var userSurname by remember { mutableStateOf("") }
    var userPhone by remember {mutableStateOf("")}

    LaunchedEffect(uiState) {
        if(uiState is ProfileUiState.Success){
            val user = (uiState as ProfileUiState.Success).user
            userName = user.nombre
            userSurname = user.apellidos
            userPhone = user.telefono
        }
    }

    LaunchedEffect(updateStatus) {
        when(updateStatus){
            UpdateStatus.SUCCESS -> {
                Toast.makeText(context, "Perfil actualizado con exito", Toast.LENGTH_SHORT).show()
                profileViewModel.resetUpdateStatus()
                navController.navigate(Routes.PROFILE_DETAILS){
                    popUpTo(Routes.PROFILE_DETAILS){
                        inclusive = true
                    }
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

        when(uiState){
            is ProfileUiState.Loading ->{
                CircularProgressIndicator()
                Text(text = "Cargando datos")
            }
            is ProfileUiState.Error ->{
                Text(text = "Error al cargar el perfil: ${(uiState as ProfileUiState.Error).message}", color = MaterialTheme.colorScheme.error)
            }
            is ProfileUiState.Success -> {
                OutlinedTextField(
                    value =  userName,
                    onValueChange = {newValue ->
                        if (newValue.all { it.isLetter() || it.isWhitespace()} ||newValue.isBlank()){
                            userName = newValue
                        }
                    },
                    label = {Text("Nombre")},
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = userSurname,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isLetter() || it.isWhitespace()} ||newValue.isBlank()){
                            userSurname = newValue
                        }
                    },
                    label = {Text("Apellidos")},
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Phone TextField
                OutlinedTextField(
                    value = userPhone,
                    onValueChange = { newValue ->
                        if(newValue.length <= 9 && newValue.all {it.isDigit()}){
                            userPhone = newValue
                        }
                    },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = {
                    if(userName.isBlank() || userSurname.isBlank() || userPhone.isBlank()){
                        Toast.makeText(context, "Por favor completo todos los campos.", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    val updates = mutableMapOf<String, Any>()
                    val currentUser = (uiState as ProfileUiState.Success).user
                    if (userName != currentUser.nombre){
                        updates["nombre"] = userName
                    }
                    if (userSurname != currentUser.apellidos){
                        updates["apellidos"] = userSurname
                    }
                    if (userPhone.length == 9 && userPhone != currentUser.telefono){
                        updates["telefono"] = userPhone
                    } else if(userPhone.length != 9 && userPhone.isNotEmpty()){
                        Toast.makeText(context, "El numero de telefono debe tener 9 digitos", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    if(updates.isNotEmpty()){
                        profileViewModel.updateProfile(updates)
                    } else {
                        Toast.makeText(context, "No hay cambios para guardar", Toast.LENGTH_LONG).show()
                        navController.navigate(Routes.PROFILE_DETAILS){
                            popUpTo(Routes.PROFILE_DETAILS){inclusive = true}
                            launchSingleTop = true
                        }
                    }
                },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC02128)),
                    enabled = updateStatus != UpdateStatus.LOADING
                ) {
                    if (updateStatus == UpdateStatus.LOADING){
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text(text = "Guardar cambios", style = MaterialTheme.typography.titleMedium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    navController.popBackStack()
                },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    enabled = updateStatus != UpdateStatus.LOADING
                ) {
                    Text(text = "Cancelar", style = MaterialTheme.typography.titleMedium, color = Color.Black)
                }
            }
        }

    }

}