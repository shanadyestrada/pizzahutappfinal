package com.example.pizzahutappfinal.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pizzahutappfinal.AppUtil
import com.example.pizzahutappfinal.components.CartItemView
import com.example.pizzahutappfinal.components.CheckoutViewModel

@Composable
fun CheckoutPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    checkoutViewModel: CheckoutViewModel = viewModel()
) {
    val context = LocalContext.current
    val cartItems = checkoutViewModel.cartItems.observeAsState(initial = emptyList()).value
    val totalPrice = checkoutViewModel.totalPrice.observeAsState(initial = 0.0).value
    val isLoading = checkoutViewModel.isLoading.observeAsState(initial = true).value

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título de la página
        Text(
            text = "Tu Pedido",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Contenido principal de la página
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("El carrito está vacío", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            // Lista de productos del carrito
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(cartItems) { index, cartItem ->
                    CartItemView(cartItem = cartItem)
                }
            }

            // Sección del total y el botón de pago
            Spacer(modifier = Modifier.height(16.dp))

            // Fila para mostrar el total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("S/. %.2f".format(totalPrice), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de Pagar
            Button(
                onClick = {
                    AppUtil.saveOrder(context = context, navController = navController)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pagar", fontSize = 18.sp)
            }
        }
    }
}