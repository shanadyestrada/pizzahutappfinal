package com.example.pizzahutappfinal.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pizzahutappfinal.AppUtil
import com.example.pizzahutappfinal.GlobalNavigation
import com.example.pizzahutappfinal.model.ProductModel
import com.example.pizzahutappfinal.model.TamanoModel
import com.example.pizzahutappfinal.ui.theme.SharpSansFontFamily

@Composable
fun ProductItemView(modifier: Modifier = Modifier, product: ProductModel) {

    val primaryColor = Color(0xFFA90A24)
    val context = LocalContext.current

    var isDropdownExpanded by remember { mutableStateOf(false) }
    var selectedVariation by remember { mutableStateOf<Pair<String, String>?>(null) }
    var selectedPrice by remember { mutableStateOf(0.0) }

    LaunchedEffect(product) {
        if (product.categoria == "pizzas" && product.variaciones != null) {
            val defaultSize = "grande"
            val defaultCrust = "Artesanal"
            val defaultPrice = product.variaciones.grande?.Artesanal
            selectedVariation = Pair(defaultSize, defaultCrust)
            selectedPrice = defaultPrice?.toDoubleOrNull() ?: 0.0
        } else {
            selectedPrice = product.precio.toDoubleOrNull() ?: 0.0
        }
    }

    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = product.image,
                contentDescription = product.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = product.nombre.uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Clip
                )

                if (product.descripcion.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.descripcion,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.sp

                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "S/. ${String.format("%.2f", selectedPrice)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFAF0014)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (product.categoria == "pizzas" && product.variaciones != null) {
                    Text(
                        "SELECCIONA TAMAÑO Y CORTEZA:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box {
                        OutlinedButton (
                            onClick = { isDropdownExpanded = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Black, RoundedCornerShape(4.dp)),
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Gray,
                                containerColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp)
                        ) {
                            val selectedText = selectedVariation?.let {
                                "${it.first.replaceFirstChar { char -> char.uppercase() }} - ${it.second.replaceFirstChar { char -> char.uppercase() }}"
                            } ?: "Selecciona una opción"

                            Text(
                                text = selectedText,
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.Black,
                                fontFamily = SharpSansFontFamily,
                                textAlign = TextAlign.Start
                            )
                        }

                        DropdownMenu (
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                        ) {
                            product.variaciones?.familiar?.let { tamano ->
                                addCrustMenuItems("familiar", tamano, onSelect = { size, crust, price ->
                                    selectedVariation = Pair(size, crust)
                                    selectedPrice = price.toDoubleOrNull() ?: 0.0
                                    isDropdownExpanded = false
                                })
                            }
                            product.variaciones?.grande?.let { tamano ->
                                addCrustMenuItems("grande", tamano, onSelect = { size, crust, price ->
                                    selectedVariation = Pair(size, crust)
                                    selectedPrice = price.toDoubleOrNull() ?: 0.0
                                    isDropdownExpanded = false
                                })
                            }
                            product.variaciones?.mediana?.let { tamano ->
                                addCrustMenuItems("mediana", tamano, onSelect = { size, crust, price ->
                                    selectedVariation = Pair(size, crust)
                                    selectedPrice = price.toDoubleOrNull() ?: 0.0
                                    isDropdownExpanded = false
                                })
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column (
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp) // Añade espacio entre los botones
                ) {
                    // Botón AGREGAR para todos los productos
                    Button(
                        onClick = {
                            val variationKey = if (product.categoria == "pizzas") {
                                selectedVariation?.let { "${it.first}_${it.second}" }
                            } else {
                                null
                            }
                            AppUtil.addToCart(context, product.id, variationKey)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "AGREGAR",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = SharpSansFontFamily,
                            letterSpacing = 2.sp,
                            color = Color.White
                        )
                    }

                    // Botón PERSONALIZAR, solo visible para pizzas
                    if (product.categoria == "pizzas") {
                        Button(
                            onClick = { GlobalNavigation.navController.navigate("product-details/" + product.id) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color(0xFFAF0014)
                            ),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, Color(0xFFAF0014))
                        ) {
                            Text(
                                text = "PERSONALIZAR",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = SharpSansFontFamily,
                                letterSpacing = 2.sp
                            )
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun addCrustMenuItems(
    size: String,
    tamanoModel: TamanoModel,
    onSelect: (String, String, String) -> Unit
) {
    val crusts = listOf(
        "Artesanal" to tamanoModel.Artesanal,
        "CheeseBites" to tamanoModel.CheeseBites,
        "Delgada" to tamanoModel.Delgada,
        "HutCheese" to tamanoModel.HutCheese,
        "Pan" to tamanoModel.Pan,
    )

    crusts.forEach { (crustName, price) ->
        if (!price.isNullOrEmpty()) {
            DropdownMenuItem(
                onClick = { onSelect(size, crustName, price) },
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${size.replaceFirstChar { it.uppercase() }} - ${crustName.replaceFirstChar { it.uppercase() }}",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontFamily = SharpSansFontFamily
                        )
                        Text(
                            text = "S/ $price",
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            fontFamily = SharpSansFontFamily
                        )
                    }
                }
            )
        }
    }
}

