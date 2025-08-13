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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.pizzahutappfinal.model.getTamano
import com.example.pizzahutappfinal.ui.theme.BrixtonLeadFontFamily
import com.example.pizzahutappfinal.ui.theme.SharpSansFontFamily
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

@Composable
fun ProductDetailsPage(modifier: Modifier = Modifier, productId: String) {

    var product by remember { mutableStateOf(ProductModel()) }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    val primaryColor = Color(0xFFA90A24)
    val navController = GlobalNavigation.navController

    var isDropdownExpanded by remember { mutableStateOf(false) }
    var selectedVariation by remember { mutableStateOf<Pair<String, String>?>(null) }
    var selectedPrice by remember { mutableStateOf(0.0) }

    var selectedAdicionales by remember { mutableStateOf(emptySet<String>()) }

    LaunchedEffect(productId) {
        isLoading = true // 🆕 Al iniciar la carga, establecemos isLoading en true
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
                selectedPrice = defaultPrice?.toDoubleOrNull() ?: 0.0
            } else {
                selectedPrice = it.precio.toDoubleOrNull() ?: 0.0
            }
        }
        isLoading = false // 🆕 Al terminar la carga, establecemos isLoading en false
    }
    LaunchedEffect(selectedVariation, selectedAdicionales) {
        if (product.categoria.lowercase() == "pizzas" && selectedVariation != null) {
            val (size, crust) = selectedVariation!!
            val tamanoModel = product.variaciones?.getTamano(size)

            val basePrice = tamanoModel?.let {
                when (crust.lowercase()) {
                    "artesanal" -> it.Artesanal
                    "cheesebites" -> it.CheeseBites
                    "delgada" -> it.Delgada
                    "hutcheese" -> it.HutCheese
                    "pan" -> it.Pan
                    else -> null
                }
            }?.toDoubleOrNull() ?: 0.0

            val additionalPrice = product.adicionales
                .filterKeys { selectedAdicionales.contains(it) }
                .values
                .sumOf {  it.toDoubleOrNull() ?: 0.0 }

            selectedPrice = basePrice + additionalPrice
        }
    }

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
                tint = Color(0xFFA90A24)
            )
            Text(
                text = "Volver",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFA90A24)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = primaryColor)
                }
            } else {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (product.categoria.lowercase() == "pizzas") {
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

                            val imageResId = AppUtil.CrustImages.images[corteza]
                            if (imageResId != null) {
                                CrustCardVisual(
                                    nombre = corteza,
                                    adicional = adicional,
                                    imageResId = imageResId
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                }
                if (product.categoria == "pizzas" && product.variaciones != null) {

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
                                addCrustMenuItems(
                                    "familiar",
                                    tamano,
                                    onSelect = { size, crust, price ->
                                        selectedVariation = Pair(size, crust)
                                        selectedPrice = price.toDoubleOrNull() ?: 0.0
                                        isDropdownExpanded = false
                                    })
                            }
                            product.variaciones?.grande?.let { tamano ->
                                addCrustMenuItems(
                                    "grande",
                                    tamano,
                                    onSelect = { size, crust, price ->
                                        selectedVariation = Pair(size, crust)
                                        selectedPrice = price.toDoubleOrNull() ?: 0.0
                                        isDropdownExpanded = false
                                    })
                            }
                            product.variaciones?.mediana?.let { tamano ->
                                addCrustMenuItems(
                                    "mediana",
                                    tamano,
                                    onSelect = { size, crust, price ->
                                        selectedVariation = Pair(size, crust)
                                        selectedPrice = price.toDoubleOrNull() ?: 0.0
                                        isDropdownExpanded = false
                                    })
                            }
                        }
                    }


                }

                Spacer(modifier = Modifier.height(16.dp))

                if (product.adicionales.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Complementa tu pizza con nuestros ingredientes:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 19.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        product.adicionales.forEach { (adicional, precio) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedAdicionales =
                                            if (selectedAdicionales.contains(adicional)) {
                                                selectedAdicionales - adicional
                                            } else {
                                                selectedAdicionales + adicional
                                            }
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedAdicionales.contains(adicional),
                                    onCheckedChange = { isChecked ->
                                        selectedAdicionales = if (isChecked) {
                                            selectedAdicionales + adicional
                                        } else {
                                            selectedAdicionales - adicional
                                        }
                                    },
                                    // 🆕 Se cambian los colores del Checkbox
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFFA90A24),
                                        uncheckedColor = Color(0xFFA90A24),
                                        checkmarkColor = Color.White
                                    )
                                )
                                Text(
                                    text = adicional.replaceFirstChar { it.uppercase() },
                                    modifier = Modifier.weight(1f),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "+ S/. %.2f".format(precio.toDoubleOrNull() ?: 0.0),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))

                val additionalPrice = product.adicionales
                    .filterKeys { selectedAdicionales.contains(it) }
                    .values
                    .sumOf { it.toDoubleOrNull() ?: 0.0 }
                val finalPrice = selectedPrice + additionalPrice

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontFamily = SharpSansFontFamily
                        )
                        Text(
                            text = "S/. %.2f".format(finalPrice),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFAF0014),
                            fontFamily = SharpSansFontFamily
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val variationKey =
                                if (product.categoria == "pizzas" && selectedVariation != null) {
                                    "${selectedVariation!!.first}_${selectedVariation!!.second}"
                                } else {
                                    null
                                }
                            AppUtil.addToCart(
                                context,
                                productId,
                                variationKey,
                                selectedAdicionales.toList()
                            )
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier.fillMaxWidth(),
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

            }   }

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
                fontSize = 14.sp,
                fontFamily = SharpSansFontFamily
            )
            Text(
                text = "$rebanadas slices",
                fontSize = 10.sp,
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
    imageResId: Int,
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
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            AsyncImage(
                model = imageResId,
                contentDescription = "Imagen de $nombre",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = nombre,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f),
                fontFamily = SharpSansFontFamily,
                color = Color.Black
            )

            if (adicional != null) {
                Text(
                    text = "+ S/$adicional",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SharpSansFontFamily,
                    color = Color.Gray
                )
            }
        }
    }
}