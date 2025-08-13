package com.example.pizzahutappfinal.pages

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzahutappfinal.GlobalNavigation
import com.example.pizzahutappfinal.R
import com.example.pizzahutappfinal.model.LocalModel
import com.example.pizzahutappfinal.ui.theme.BrixtonLeadFontFamily
import com.example.pizzahutappfinal.ui.theme.SharpSansFontFamily
import com.example.pizzahutappfinal.viewmodel.LocalViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import com.example.pizzahutappfinal.AppUtil

@Composable
fun LocalPage(modifier: Modifier = Modifier, localViewModel: LocalViewModel = viewModel()) {
    val primaryColor = Color(0xFFA90A24)
    val isLoading by localViewModel.isLoading.observeAsState(initial = true)
    val locales by localViewModel.locales.observeAsState(initial = emptyList())

    var expanded by remember { mutableStateOf(false) }
    var selectedLocal by remember { mutableStateOf<LocalModel?>(null) }

    LaunchedEffect(key1 = locales) {
        if (locales.isNotEmpty() && selectedLocal == null) {
            selectedLocal = locales.first()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Image(
                painter = painterResource(id = R.drawable.vector),
                contentDescription = "Logo",
                modifier = Modifier.size(61.dp)
            )
            Text(
                text = "NUESTROS LOCALES",
                fontFamily = BrixtonLeadFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 38.sp,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = primaryColor)
            }
        } else if (locales.isEmpty()) {
            Text(
                text = "No hay locales disponibles.",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Selecciona la tienda de tu preferencia",
                    fontFamily = SharpSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selectedLocal != null) {
                            LocalComboBoxItem(local = selectedLocal!!)
                        } else {
                            Text(
                                text = "Seleccione un local",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Expandir")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        locales.forEach { local ->
                            DropdownMenuItem(
                                text = { LocalComboBoxItem(local) },
                                onClick = {
                                    selectedLocal = local
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedLocal != null) {
                    SelectedLocalInfo(local = selectedLocal!!)
                }
            }
        }
    }
}

@Composable
fun LocalComboBoxItem(local: LocalModel) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Icono de local",
            tint = Color(0xFFA90A24),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = local.nombre,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = SharpSansFontFamily
            )
            Text(
                text = local.direccion,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                fontFamily = SharpSansFontFamily,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun SelectedLocalInfo(local: LocalModel) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ✅ Imagen más grande
        Image(
            painter = rememberAsyncImagePainter(model = local.img),
            contentDescription = "Imagen del local ${local.nombre}",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para abrir el mapa
        Button(onClick = {
            AppUtil.openMapsForLocal(context, local.latitud, local.longitud)
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color(0xFFAF0014)
            ),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, Color(0xFFAF0014))
        ) {
            Text("VER EN MAPA",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = SharpSansFontFamily,
                letterSpacing = 2.sp)
        }
    }
}