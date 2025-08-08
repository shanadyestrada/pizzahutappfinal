package com.example.pizzahutappfinal.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pizzahutappfinal.GlobalNavigation
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Contenido de Home")

        Button (onClick = {
            Firebase.auth.signOut()
            GlobalNavigation.navController.navigate("auth") {
                popUpTo("home") { inclusive = true}
            }
        }
        ) {
            Text(text = "Cerrar Sesión")
        }
    }
}
