package com.example.pizzahutappfinal.pages

import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.pizzahutappfinal.GlobalNavigation.navController
import com.example.pizzahutappfinal.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

object Routes {
    const val MY_ACCOUNT = "my_account"
    const val PROFILE_DETAILS = "profile_details"
    const val MY_ADDRESSES = "my_addresses"
    const val MY_ORDERS = "my_orders"
    const val LOGIN = "login" // Assuming a login screen for logout redirection
}

@Composable
fun MyAccountPage(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.vector),
            contentDescription = stringResource(R.string.pizza_hut_logo_description)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Mi Cuenta",
            style = MaterialTheme.typography.headlineLarge.copy(color = Color.Black)
        )
        Spacer(modifier = Modifier.height(48.dp)) // Space below title

        AccountActionButton(
            text = "Mi información",
            onClick = { navController.navigate(Routes.PROFILE_DETAILS) }, // Direct navigation
            backgroundColor = Color(0xFFC02128) // Pizza Hut Red
        )
        Spacer(modifier = Modifier.height(16.dp))

        AccountActionButton(
            text = "Mis direcciones",
            onClick = { navController.navigate(Routes.MY_ADDRESSES) }, // Direct navigation
            backgroundColor = Color(0xFFC02128) // Pizza Hut Red
        )
        Spacer(modifier = Modifier.height(16.dp))

        AccountActionButton(
            text = "Ver mis pedidos",
            onClick = { navController.navigate(Routes.MY_ORDERS) }, // Direct navigation
            backgroundColor = Color(0xFFC02128) // Pizza Hut Red
        )
        Spacer(modifier = Modifier.height(32.dp)) // More space before logout button

        AccountActionButton(
            text = "Cerrar Sesión",
            onClick = {
                Firebase.auth.signOut()
                navController.navigate("auth") {

                    popUpTo("home") { inclusive = true }
                }
            },
            backgroundColor = Color.Black,
            textColor = Color.White
        )
    }
}

@Composable
private fun AccountActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    textColor: Color = Color.White // Default to white text
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth() // Makes button take full width
            .height(56.dp), // Fixed height for consistency
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium, color = textColor)
    }
}