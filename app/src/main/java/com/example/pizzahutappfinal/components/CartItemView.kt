package com.example.pizzahutappfinal.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import com.example.pizzahutappfinal.AppUtil
import com.example.pizzahutappfinal.model.CartItemModel
import com.example.pizzahutappfinal.model.ProductModel
import com.example.pizzahutappfinal.model.getTamano
import com.example.pizzahutappfinal.ui.theme.BrixtonLeadFontFamily
import com.example.pizzahutappfinal.ui.theme.SharpSansFontFamily
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun CartItemView( modifier: Modifier = Modifier, cartItem: CartItemModel) {
    var product by remember { mutableStateOf(ProductModel()) }
    val context = LocalContext.current
    val productId = cartItem.productoId
    val variationKey = cartItem.variaciones
    val qty = cartItem.cantidad
    val adicionales = cartItem.adicionales

    val (size, crust) = if (variationKey != null) {
        val variationParts = variationKey.split("_")
        Pair(variationParts[0], variationParts[1])
    } else {
        Pair("", "")
    }

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("data")
            .document("stock").collection("productos")
            .document(productId).get()
            .addOnSuccessListener {
                it.toObject(ProductModel::class.java)?.let { result ->
                    product = result
                }
            }
    }

    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Línea 1: Nombre y Precio
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                val nombreFormateado = if (product.variaciones != null) {
                    "${product.nombre.uppercase()} ${size.replaceFirstChar { it.uppercase() }}"
                } else {
                    product.nombre.uppercase()
                }

                Text(
                    text = nombreFormateado,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                val priceString = if (product.categoria.lowercase() == "pizzas" && variationKey != null) {
                    val (size, crust) = variationKey.split("_")
                    val tamanoModel = product.variaciones?.getTamano(size)
                    tamanoModel?.let {
                        when (crust.lowercase()) {
                            "artesanal" -> it.Artesanal
                            "cheesebites" -> it.CheeseBites
                            "delgada" -> it.Delgada
                            "hutcheese" -> it.HutCheese
                            "pan" -> it.Pan
                            else -> null
                        }
                    }
                } else {
                    product.precio
                }

                val basePrice = priceString?.toDoubleOrNull() ?: 0.0

                val additionalPrice = product.adicionales
                    .filterKeys { adicionales.contains(it) }
                    .values
                    .sumOf { it.toDoubleOrNull() ?: 0.0 }

                val itemTotalPrice = (basePrice + additionalPrice) * qty

                Text(
                    text = "S/. %.2f".format(itemTotalPrice),
                    fontWeight = FontWeight.Bold,
                    fontFamily = SharpSansFontFamily,
                    fontSize = 16.sp
                )
            }

            // Línea 2: Nombre de la corteza (debajo del nombre principal)
            if (product.categoria == "pizzas" && variationKey != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = crust.replaceFirstChar { it.uppercase() },
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                    // Mostrar el costo adicional solo si la corteza es CheeseBites o HutCheese
                    if (crust == "CheeseBites" || crust == "HutCheese") {
                        Text(
                            text = "+ S/5.00",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // 🆕 Nueva Sección de Adicionales
            if (adicionales.isNotEmpty()) {
                adicionales.forEach { adicional ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = adicional.replaceFirstChar { it.uppercase() },
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        val additionalPrice = product.adicionales[adicional]?.toDoubleOrNull() ?: 0.0
                        Text(
                            text = "+ S/. %.2f".format(additionalPrice),
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Línea 2: Eliminar y cantidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón eliminar
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            AppUtil.removeFromCart(context, productId, variationKey, adicionales = cartItem.adicionales,  removeAll = true)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar del carrito",
                        tint = Color.Red
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Botón "-"
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFFFFCDD2), CircleShape) // fondo rojo claro
                        .clickable {
                            AppUtil.removeFromCart(context, productId, variationKey, adicionales = cartItem.adicionales)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "-",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Cantidad
                Text(
                    text = "$qty",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Botón "+"
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFFC8E6C9), CircleShape) // fondo verde claro
                        .clickable {
                            AppUtil.addToCart(context, productId, variationKey, adicionales = cartItem.adicionales)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}