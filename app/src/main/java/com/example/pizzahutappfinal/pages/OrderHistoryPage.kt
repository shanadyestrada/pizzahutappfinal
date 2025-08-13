package com.example.pizzahutappfinal.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pizzahutappfinal.GlobalNavigation
import com.example.pizzahutappfinal.R
import com.example.pizzahutappfinal.components.OrderSummaryItem
import com.example.pizzahutappfinal.viewmodel.OrderHistoryViewModel
import com.example.pizzahutappfinal.ui.theme.BrixtonLeadFontFamily
import com.example.pizzahutappfinal.ui.theme.SharpSansFontFamily


@Composable
fun OrderHistoryPage(navController: NavController, viewModel: OrderHistoryViewModel = viewModel()) {
    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column (modifier = Modifier.fillMaxWidth())  {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .background(Color(0xFFAF0014))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Pizza Hut",
                modifier = Modifier.size(34.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                .clickable {
                    GlobalNavigation.navController.popBackStack()
                }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = Color(0xFFA90A24) // Color rojo personalizado
            )
            Text(
                text = "Volver",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFA90A24)
            )
        }

        Column (modifier = Modifier.padding(horizontal = 16.dp)){
            // Sombrero/logo
            Image(
                painter = painterResource(id = R.drawable.vector),
                contentDescription = "Logo",
                modifier = Modifier.size(61.dp)
            )

            // Título
            Text(
                text = "MIS PEDIDOS",
                fontFamily = BrixtonLeadFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 38.sp,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Column (modifier = Modifier.padding(horizontal = 16.dp)) {
                if (isLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color(0xFFA90A24))
                    }
                } else if (orders.isEmpty()) {
                    Text("No tienes pedidos realizados aún.")
                } else {
                    orders.forEach { order ->
                        OrderSummaryItem(
                            order = order,
                            onClick = { navController.navigate("invoiceFromOrdersPage/${order.orderId}") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // ✅ Spacer para la línea izquierda
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color.Gray)
                    )

                    // ✅ Texto del contador
                    Text(
                        text = "${orders.size} pedido(s) realizado(s)",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // ✅ Spacer para la línea derecha
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color.Gray)
                    )
                }
            }
        }
    }

}