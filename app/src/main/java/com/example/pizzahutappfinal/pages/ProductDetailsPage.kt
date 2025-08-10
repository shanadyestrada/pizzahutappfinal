package com.example.pizzahutappfinal.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pizzahutappfinal.AppUtil
import com.example.pizzahutappfinal.R
import com.example.pizzahutappfinal.GlobalNavigation
import com.example.pizzahutappfinal.model.ProductModel
import com.example.pizzahutappfinal.model.TamanoModel
import com.example.pizzahutappfinal.ui.theme.BrixtonLeadFontFamily
import com.example.pizzahutappfinal.ui.theme.SharpSansFontFamily
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

@Composable
fun ProductDetailsPage(modifier: Modifier = Modifier, productId: String) {

    var product by remember { mutableStateOf(ProductModel()) }
    val context = LocalContext.current

    // Nuevo estado para controlar la visibilidad del menú desplegable.
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var selectedVariation by remember { mutableStateOf<Pair<String, String>?>(null) }
    var selectedPrice by remember { mutableStateOf("") }

    LaunchedEffect(productId) {
        val snapshot = Firebase.firestore
            .collection("data").document("stock")
            .collection("productos")
            .document(productId)
            .get()
            .await()

        snapshot.toObject(ProductModel::class.java)?.let {
            product = it
            if (it.categoria == "pizzas" && it.variaciones != null) {
                val defaultSize = "grande"
                val defaultCrust = "Artesanal"
                val defaultPrice = it.variaciones.grande?.Artesanal
                selectedVariation = Pair(defaultSize, defaultCrust)
                selectedPrice = (defaultPrice ?: "").toString()
            } else {
                selectedPrice = it.precio
            }
        }
    }


    // Estructura con Scaffold para fijar el botón
    Scaffold (
        bottomBar = {
            Button(
                onClick = {
                    val variationKey = if (product.categoria == "pizzas" && selectedVariation != null) {
                        "${selectedVariation!!.first}_${selectedVariation!!.second}"
                    } else {
                        null
                    }
                    AppUtil.addToCart(context, productId, variationKey)
                },
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAF0014))
            ) {
                Text(
                    "AGREGAR AL CARRITO",
                    modifier = Modifier.padding(5.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SharpSansFontFamily,
                    letterSpacing = 2.sp
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // deja espacio al bottomBar
        ) {
            // HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(Color(0xFFAF0014))
                    .padding(horizontal = 16.dp,  vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mi Header",
                    color = Color.White
                )
            }

            // FLECHA VOLVER
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clickable {
                        GlobalNavigation.navController.popBackStack()
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color(0xFFA90A24)
                )
                Text(
                    text = "Volver",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFA90A24)
                )
            }

            // CONTENIDO SCROLLEABLE
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                AsyncImage(
                    model = product.image,
                    contentDescription = product.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = product.nombre,
                    fontFamily = BrixtonLeadFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 42.sp,
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = product.descripcion,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )

                // AÑADIMOS ESTA SECCIÓN PARA LAS TARJETAS VISUALES
                if (product.categoria.lowercase() == "pizzas") {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "TAMAÑOS DISPONIBLES",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp,
                        fontFamily = SharpSansFontFamily
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        val cardModifier = Modifier
                            .weight(1f)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(Color.White)
                        // Tarjeta para Familiar
                        if (product.variaciones?.familiar != null) {
                            CardTamanoVisual(
                                modifier = cardModifier,
                                nombre = "Familiar",
                                rebanadas = 12
                            )
                        }
                        // Tarjeta para Grande
                        if (product.variaciones?.grande != null) {
                            CardTamanoVisual(
                                modifier = cardModifier,
                                nombre = "Grande",
                                rebanadas = 8
                            )
                        }
                        // Tarjeta para Mediana
                        if (product.variaciones?.mediana != null) {
                            CardTamanoVisual(
                                modifier = cardModifier,
                                nombre = "Mediana",
                                rebanadas = 6
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // === SECCIÓN VISUAL DE CORTEZAS ===
                if (product.categoria.lowercase() == "pizzas" && product.variaciones != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "CORTEZAS DISPONIBLES",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp,
                        fontFamily = SharpSansFontFamily
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val crustasList = mutableSetOf<String>()

                    val tamanoList = listOfNotNull(
                        product.variaciones?.mediana,
                        product.variaciones?.grande,
                        product.variaciones?.familiar
                    )

                    tamanoList.forEach { tamano ->
                        if (tamano.Artesanal.isNotBlank()) crustasList.add("Artesanal")
                        if (tamano.Delgada.isNotBlank()) crustasList.add("Delgada")
                        if (tamano.Pan.isNotBlank()) crustasList.add("Pan")
                        if (tamano.CheeseBites.isNotBlank()) crustasList.add("CheeseBites")
                        if (tamano.HutCheese.isNotBlank()) crustasList.add("HutCheese")
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        crustasList.forEach { corteza ->
                            val adicional = when (corteza) {
                                "CheeseBites", "HutCheese" -> "5.00"
                                else -> null
                            }
                            CrustCardVisual(
                                nombre = corteza,
                                adicional = adicional
                            )
                        }
                    }

                }

                // Combobox y precio para productos con variaciones
                if (product.categoria == "pizzas" && product.variaciones != null) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "SELECCIONA TAMAÑO Y CORTEZA:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Box {
                        OutlinedButton(
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

                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                        ) {
                            product.variaciones?.familiar?.let { tamano ->
                                addCrustMenuItems("familiar", tamano, onSelect = { size, crust, price ->
                                    selectedVariation = Pair(size, crust)
                                    selectedPrice = price
                                    isDropdownExpanded = false
                                })
                            }
                            product.variaciones?.grande?.let { tamano ->
                                addCrustMenuItems("grande", tamano, onSelect = { size, crust, price ->
                                    selectedVariation = Pair(size, crust)
                                    selectedPrice = price
                                    isDropdownExpanded = false
                                })
                            }
                            product.variaciones?.mediana?.let { tamano ->
                                addCrustMenuItems("mediana", tamano, onSelect = { size, crust, price ->
                                    selectedVariation = Pair(size, crust)
                                    selectedPrice = price
                                    isDropdownExpanded = false
                                })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Precio: $$selectedPrice",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFAF0014)
                    )

                } else {
                    // Precio para productos sin variaciones
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Precio: $$selectedPrice",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFAF0014)
                    )
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

@Composable
fun CardTamanoVisual(
    modifier: Modifier = Modifier,
    nombre: String,
    rebanadas: Int
) {
    Box (
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 2.dp,
                color = Color(0xFFAF0014),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = nombre,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                fontFamily = SharpSansFontFamily
            )
            Text(
                text = "$rebanadas slices",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = SharpSansFontFamily,
                color = Color.DarkGray
            )
        }

    }
}

@Composable
fun CrustCardVisual(
    modifier: Modifier = Modifier,
    nombre: String,
    adicional: String? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Recuadro gris para la imagen
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Contenido de texto
            Text(
                text = nombre,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f),
                fontFamily = SharpSansFontFamily,
                color = Color.Black
            )

            // Precio adicional
            if (adicional != null) {
                Text(
                    text = "+ S/$adicional",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SharpSansFontFamily,
                    color = Color.Gray // Color rojo
                )
            }
        }
    }
}