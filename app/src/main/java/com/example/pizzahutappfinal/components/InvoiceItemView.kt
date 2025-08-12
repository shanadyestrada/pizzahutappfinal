package com.example.pizzahutappfinal.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahutappfinal.model.CartItemModel
import com.example.pizzahutappfinal.model.getTamano
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.example.pizzahutappfinal.ui.theme.SharpSansFontFamily

import com.example.pizzahutappfinal.model.ProductModel
import com.example.pizzahutappfinal.model.getPrecioCorteza

@Composable
fun InvoiceItemView(cartItem: CartItemModel) {
    var product by remember { mutableStateOf<ProductModel?>(null) }
    val productId = cartItem.productoId
    val cantidad = cartItem.cantidad
    val variationKey = cartItem.variaciones
    val adicionales = cartItem.adicionales

    val (size, crust) = if (variationKey != null) {
        val variationParts = variationKey.split("_")
        Pair(variationParts[0].replaceFirstChar { it.uppercase() }, variationParts[1].replaceFirstChar { it.uppercase() })
    } else {
        Pair("", "")
    }

    LaunchedEffect (productId) {
        Firebase.firestore.collection("data")
            .document("stock").collection("productos")
            .document(productId).get()
            .addOnSuccessListener { documentSnapshot ->
                product = documentSnapshot.toObject(ProductModel::class.java)
            }
    }

    product?.let { productModel ->
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            // Línea 1: Nombre del producto y cantidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nombre del producto + tamaño si es una pizza
                val nombreFormateado = if (productModel.variaciones != null && !size.isNullOrEmpty()) {
                    "${productModel.nombre.uppercase()} ($size)"
                } else {
                    productModel.nombre.uppercase()
                }

                Text(
                    text = "$nombreFormateado x$cantidad",
                    fontFamily = SharpSansFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )

                val basePriceString = if (productModel.categoria.lowercase() == "pizzas" && !variationKey.isNullOrEmpty()) {
                    val tamanoModel = productModel.variaciones?.getTamano(size.lowercase())
                    tamanoModel?.getPrecioCorteza(crust.lowercase())
                } else {
                    productModel.precio
                }
                val basePrice = basePriceString?.toDoubleOrNull() ?: 0.0

                val additionalPrice = productModel.adicionales
                    .filterKeys { adicionales.contains(it) }
                    .values
                    .sumOf { it.toDoubleOrNull() ?: 0.0 }

                val itemTotalPrice = (basePrice + additionalPrice) * cantidad

                // Precio base (simplificado)
                Text(
                    text = "S/. %.2f".format(itemTotalPrice),
                    fontFamily = SharpSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            // Línea 2: Corteza de la pizza si existe
            if (productModel.categoria.lowercase() == "pizzas" && !crust.isNullOrEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$crust",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                    // Precio adicional de la corteza (CheeseBites o HutCheese)
                    if (crust == "CheeseBites" || crust == "HutCheese") {
                        Text(
                            text = "+ S/. 5.00",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Sección de Adicionales
            if (adicionales.isNotEmpty()) {
                adicionales.forEach { adicional ->
                    val additionalPrice = productModel.adicionales[adicional]?.toDoubleOrNull() ?: 0.0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${adicional.replaceFirstChar { it.uppercase() }}",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "+ S/. %.2f".format(additionalPrice),
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}