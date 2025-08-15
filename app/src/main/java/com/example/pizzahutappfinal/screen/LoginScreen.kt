package com.example.pizzahutappfinal.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton // Importar TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pizzahutappfinal.AppUtil
import com.example.pizzahutappfinal.R
import com.example.pizzahutappfinal.ui.theme.BrixtonLeadFontFamily
import com.example.pizzahutappfinal.viewmodel.AuthViewModel
import androidx.compose.ui.text.input.VisualTransformation
import com.example.pizzahutappfinal.ui.theme.BrixtonLeadFontFamily

@Composable
fun LoginScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel = viewModel()) {

    val primaryColor = Color(0xFFA90A24)

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    Column (modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){

        // Logo de Pizza Hut (Asegúrate de tener un recurso de drawable para tu logo, ej: R.drawable.pizza_hut_logo)
        Image(
            painter = painterResource(id = R.drawable.vector), // Reemplaza 'pizza_hut_logo' con el nombre de tu archivo de logo
            contentDescription = "Pizza Hut Logo",
            modifier = Modifier.size(110.dp)// Ajusta el tamaño del logo
        )


        // Título "INICIAR SESIÓN"
        Text(
            text = "INICIAR SESIÓN",
            style = TextStyle(
                fontSize = 38.sp,
                textAlign = TextAlign.Center,
                fontFamily = BrixtonLeadFontFamily,
                color = Color.Black // Color del texto del título
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp)) // Espacio antes de los campos de texto

        // Campo de Correo electrónico
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Correo electrónico *") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = primaryColor,
                unfocusedLabelColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(10.dp)) // Espacio entre campos

        // Campo de Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = primaryColor,
                unfocusedLabelColor = Color.Gray
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val imagePainter = if (passwordVisible)
                    painterResource(id= R.drawable.ic_visibility)
                else
                    painterResource(id = R.drawable.ic_visibility_off)

                val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                IconButton(onClick = {passwordVisible = !passwordVisible}) {
                    Icon(painter = imagePainter, contentDescription = description, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }


        )

        Spacer(modifier = Modifier.height(14.dp)) // Espacio antes del botón Ingresar

        // Botón "INGRESAR"
        Button(
            onClick = {
                isLoading = true
                authViewModel.login(email, password) { sucess, errorMessage ->
                    if (sucess) {
                        isLoading = false
                        navController.navigate("home") {
                            popUpTo("auth") { // O el ID de la ruta de autenticación
                                inclusive = true
                            }
                        }
                    } else {
                        isLoading = false
                        AppUtil.showToast(context, errorMessage ?: "Algo salió mal.")
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp), // Altura del botón
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC70000)) // Color rojo específico de Pizza Hut
        ) {
            Text(
                text = if(isLoading) "Iniciando sesión..." else "INGRESAR",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))



        Spacer(modifier = Modifier.height(16.dp))

        // Enlace "¿Usuario nuevo? Crea tu cuenta aquí"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center, // Centra el texto
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "¿Usuario nuevo? ", fontSize = 14.sp)
            TextButton(onClick = {
                navController.navigate("signup")
            }) {
                Text(
                    text = "Crea tu cuenta aquí",
                    color = primaryColor,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}