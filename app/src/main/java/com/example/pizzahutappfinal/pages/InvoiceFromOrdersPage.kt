package com.example.pizzahutappfinal.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahutappfinal.R
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pizzahutappfinal.GlobalNavigation
import com.example.pizzahutappfinal.components.InvoiceItemView
import com.example.pizzahutappfinal.ui.theme.BrixtonLeadFontFamily
import com.example.pizzahutappfinal.ui.theme.SharpSansFontFamily

import com.example.pizzahutappfinal.components.InvoiceViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun InvoiceFromOrdersPage(orderId: String, navController: NavController, modifier: Modifier = Modifier,
                invoiceViewModel: InvoiceViewModel = viewModel(factory = InvoiceViewModel.InvoiceViewModelFactory(orderId)
                )
) {
    val order = invoiceViewModel.order.observeAsState().value
    val userProfile = invoiceViewModel.userProfile.observeAsState().value
    val isLoading = invoiceViewModel.isLoading.observeAsState(initial = true).value
    val orderTotal by invoiceViewModel.orderTotal.observeAsState(initial = 0.0)

    var showPermissionDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

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

        if (isLoading || order == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = Color(0xFFA90A24))
            }
        } else {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                // Sombrero/logo
                Image(
                    painter = painterResource(id = R.drawable.vector),
                    contentDescription = "Logo",
                    modifier = Modifier.size(61.dp)
                )

                // Título
                Text(
                    text = "RESUMEN DE LA COMPRA",
                    fontFamily = BrixtonLeadFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 38.sp,
                )

            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    InvoiceAnnotatedItemOrderPage(label = "Número de Boleta: ", value = order.orderId)

                    // Fecha y Hora
                    order.timestamp?.let { date ->
                        InvoiceAnnotatedItemOrderPage(
                            label = "Fecha y Hora: ",
                            value = SimpleDateFormat(
                                "dd/MM/yyyy HH:mm",
                                Locale.getDefault()
                            ).format(date)
                        )
                    }

                    // Comprobante
                    order.tipoComprobante?.let {
                        InvoiceAnnotatedItemOrderPage(label = "Comprobante: ", value = it.nombre)
                    }

                    // Tipo de Servicio
                    val serviceType =
                        if (order.deliveryDireccion != null) "Delivery" else "Recojo en Tienda"
                    InvoiceAnnotatedItemOrderPage(label = "Tipo de Servicio: ", value = serviceType)

                    // Dirección o Local
                    order.deliveryDireccion?.let {
                        InvoiceAnnotatedItemOrderPage(
                            label = "Dirección de Envío: ",
                            value = "${it.direccion}"
                        )
                    }
                    order.localDeRecojo?.let {
                        InvoiceAnnotatedItemOrderPage(label = "Local de Recojo: ", value = it.nombre)
                    }

                    // Método de Pago
                    order.metodoPago?.let {
                        InvoiceAnnotatedItemOrderPage(label = "Método de Pago: ", value = it.nombre)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.Gray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Detalles del Pedido (productos)
                    Text(
                        text = "DETALLES DEL PEDIDO",
                        fontFamily = SharpSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFFAF0014)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    order.cartItems.forEach { cartItem ->
                        InvoiceItemView(cartItem = cartItem)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL",
                            fontFamily = SharpSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFFAF0014)
                        )
                        Text(
                            text = "S/ %.2f".format(orderTotal),
                            fontFamily = SharpSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFFAF0014)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.Gray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val phoneNumber = "080012345"

                        val annotatedString = buildAnnotatedString {
                            append("Si tienes dudas, contáctanos al: ")
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFAF0014),
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append("0800-12345")
                            }
                        }

                        Text(
                            text = annotatedString,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                showPermissionDialog = true
                            }
                        )

                        // ✅ Cuadro de diálogo de confirmación
                        if (showPermissionDialog) {
                            AlertDialog(
                                onDismissRequest = { showPermissionDialog = false },
                                title = {
                                    Text(
                                        text = "Abrir el teclado de llamadas",
                                        fontFamily = SharpSansFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 18.sp
                                    )
                                },
                                text = {
                                    Text(
                                        text = "¿Quieres abrir la aplicación de teléfono para llamar a 0800-12345?",
                                        fontFamily = SharpSansFontFamily,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            showPermissionDialog = false
                                            try {
                                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                                    data = Uri.parse("tel:$phoneNumber")
                                                }
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                Toast.makeText(
                                                    context,
                                                    "No se pudo realizar la llamada.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(
                                                0xFFAF0014
                                            )
                                        )
                                    ) {
                                        Text(
                                            "Aceptar",
                                            fontFamily = SharpSansFontFamily,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                },
                                dismissButton = {
                                    Button(
                                        onClick = { showPermissionDialog = false },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                                    ) {
                                        Text(
                                            "Cancelar",
                                            fontFamily = SharpSansFontFamily,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            )
                        }

                    }
                }

                Spacer(modifier = Modifier.height(20.dp))


            }

        }

    }
}

@Composable
fun InvoiceAnnotatedItemOrderPage(
    label: String,
    value: String,
    labelColor: Color = Color(0xFFAF0014),
    valueColor: Color = Color.Black
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = valueColor, fontWeight = FontWeight.SemiBold)) {
                    append(label)
                }
                withStyle(style = SpanStyle(color = valueColor, fontWeight = FontWeight.Medium)) {
                    append(value)
                }
            },
            fontFamily = SharpSansFontFamily,
            fontSize = 14.sp
        )
    }
}