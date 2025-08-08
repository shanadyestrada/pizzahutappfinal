package com.example.pizzahutappfinal.screen


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart

import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

import androidx.navigation.NavController


@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {
    val navItemList = listOf(
        NavItem("Inicio", Icons.Default.Home),
        NavItem("Menú", Icons.Default.Favorite),
        NavItem("Locales", Icons.Default.LocationOn),
        NavItem("Carrito", Icons.Default.ShoppingCart),
        NavItem("Cuenta", Icons.Default.AccountCircle)
    )


}

data class NavItem (
    val label: String,
    val icon: ImageVector
)