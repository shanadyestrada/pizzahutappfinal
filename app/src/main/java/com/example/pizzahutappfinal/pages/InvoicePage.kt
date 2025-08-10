package com.example.pizzahutappfinal.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pizzahutappfinal.components.InvoiceViewModel

@Composable
fun InvoicePage(
    orderId: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    invoiceViewModel: InvoiceViewModel = viewModel(
        factory = InvoiceViewModel.InvoiceViewModelFactory(orderId)
    )
) {
    val order = invoiceViewModel.order.observeAsState().value
    val isLoading = invoiceViewModel.isLoading.observeAsState(initial = true).value

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (order == null) {
            Text("No se pudo cargar la boleta.", fontSize = 18.sp)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Boleta de Pedido",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Sección de datos del pedido
                Text("ID del Pedido: ${order.orderId}", fontSize = 16.sp)
                Text("Estado: ${order.status}", fontSize = 16.sp)
                // Usar ?.let para formatear la fecha solo si no es nula
                order.timestamp?.let { date ->
                    Text("Fecha: $date", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider()

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de productos del pedido
                Text("Productos:", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(order.cartItems) { item ->
                        Text(" - ${item.productoId} x ${item.cantidad}", fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.navigate("home") {
                            // Limpia la pila de navegación para que el usuario no pueda volver a la boleta con el botón 'atrás'
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver al inicio", fontSize = 18.sp)
                }

                // Lógica del total
                // Aquí podrías llamar a una función para calcular el total si no lo tienes guardado
            }
        }
    }
}