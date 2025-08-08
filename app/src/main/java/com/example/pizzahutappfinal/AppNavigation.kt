package com.example.pizzahutappfinal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pizzahutappfinal.screen.AuthScreen
import com.example.pizzahutappfinal.screen.HomeScreen
import com.example.pizzahutappfinal.screen.LoginScreen
import com.example.pizzahutappfinal.screen.SignUpScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "auth") {
        val isLoggedIn = Firebase.auth.currentUser!=null
        val firstPage = if(isLoggedIn)"home" else "auth"

        composable("auth") {
            AuthScreen(modifier,navController)
        }

        composable("login") {
            LoginScreen(modifier, navController)
        }

        composable("signup") {
            SignUpScreen(modifier, navController)
        }

        composable("home") {
            HomeScreen(modifier,navController)
        }
    }
}