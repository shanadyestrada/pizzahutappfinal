package com.example.pizzahutappfinal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pizzahutappfinal.pages.AddressesPage
import com.example.pizzahutappfinal.pages.MyAccountPage
import com.example.pizzahutappfinal.pages.CategoryProductsPage
import com.example.pizzahutappfinal.pages.CheckoutPage
import com.example.pizzahutappfinal.pages.EditProfilePage
import com.example.pizzahutappfinal.pages.InvoiceFromOrdersPage
import com.example.pizzahutappfinal.pages.InvoicePage
import com.example.pizzahutappfinal.pages.OrderHistoryPage
import com.example.pizzahutappfinal.pages.ProductDetailsPage
import com.example.pizzahutappfinal.screen.AuthScreen
import com.example.pizzahutappfinal.screen.HomeScreen
import com.example.pizzahutappfinal.screen.LoginScreen
import com.example.pizzahutappfinal.screen.SignUpScreen
import com.example.pizzahutappfinal.pages.ProfilePage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    GlobalNavigation.navController = navController

        val isLoggedIn = Firebase.auth.currentUser!=null
        val firstPage = if(isLoggedIn)"home" else "auth"

        NavHost(navController = navController, startDestination = firstPage) {

            composable("auth") {
                AuthScreen(modifier, navController)
            }

            composable("login") {
                LoginScreen(modifier, navController)
            }

            composable("signup") {
                SignUpScreen(modifier, navController)
            }

            composable("home") {
                HomeScreen(modifier, navController)
            }

            composable("category-products/{categoryId}") {
                var categoryId = it.arguments?.getString("categoryId")
                CategoryProductsPage(modifier, categoryId ?: "")
            }

            composable("product-details/{productId}") {
                var productId = it.arguments?.getString("productId")
                ProductDetailsPage(modifier, productId?:"")
            }

            composable("checkout") {
                CheckoutPage(navController = navController)
            }

            composable(
                "invoicePage/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId")
                if (orderId != null) {
                    InvoicePage(navController = navController, orderId = orderId)
                }
            }

            composable("account"){
                MyAccountPage(modifier, navController)
            }
            composable("profile_details"){
                ProfilePage(modifier)
            }
            composable("edit_profile"){
                EditProfilePage(modifier, navController)
            }

            composable("order_history") {
                OrderHistoryPage(navController = navController)
            }

            composable("addressesPage") {
                AddressesPage(navController = navController)
            }

            composable(
                "invoiceFromOrdersPage/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId")
                if (orderId != null) {
                    InvoiceFromOrdersPage(navController = navController, orderId = orderId)
                }
            }
        }

}

object GlobalNavigation {
    lateinit var navController : NavController
}