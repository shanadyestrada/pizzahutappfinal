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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pizzahutappfinal.GlobalNavigation
import com.example.pizzahutappfinal.R
import com.example.pizzahutappfinal.viewmodel.AddressesViewModel
import com.example.pizzahutappfinal.model.DireccionModel
import com.example.pizzahutappfinal.ui.theme.BrixtonLeadFontFamily
import com.example.pizzahutappfinal.ui.theme.SharpSansFontFamily
import kotlin.collections.forEachIndexed

@Composable
fun AddressesPage(navController: NavController, viewModel: AddressesViewModel = viewModel()) {
    val addresses by viewModel.addresses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .background(Color(0xFFAF0014))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mi Header",
                color = Color.White
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

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Image(
                painter = painterResource(id = R.drawable.vector),
                contentDescription = "Logo",
                modifier = Modifier.size(61.dp)
            )
            Text(
                text = "MIS DIRECCIONES",
                fontFamily = BrixtonLeadFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 38.sp,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // El contenido principal de la página
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally // Para centrar el contenido horizontalmente
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                if (isLoading) {
                    // ✅ Centramos el CircularProgressIndicator en un Box
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (addresses.isEmpty()) {
                    Text("No tienes direcciones guardadas.")
                    Button(onClick = { /* ... */ }) {
                        Text("Agregar Dirección")
                    }
                } else {
                    addresses.forEachIndexed { index, address ->
                        AddressItem(address = address, index = index)
                        Spacer(modifier = Modifier.height(8.dp))
                        // ✅ El Divider se muestra entre los elementos y no al final
                        if (index < addresses.size - 1) {
                            Divider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = Color.LightGray,
                                thickness = 1.dp
                            )
                        }
                    }

                    // ✅ Lógica para el mensaje y botón de "Agregar dirección"
                    if (addresses.size < 2) {
                        Divider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = Color.LightGray,
                            thickness = 1.dp
                        )
                        // ✅ El contenido de "Sabías que..." está centrado en una Column
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Sabías que puedes agregar otra dirección?",
                                fontSize = 24.sp,
                                color = Color.Gray,
                                fontFamily = BrixtonLeadFontFamily
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { /* ... */ }) {
                                Text("Agregar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddressItem(address: DireccionModel, index: Int) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "DIRECCIÓN ${index + 1}",
            fontWeight = FontWeight.Bold,
            fontFamily = SharpSansFontFamily,
            fontSize = 20.sp,
            color = Color(0xFFA90A24)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Text(text = address.nombre, fontFamily = SharpSansFontFamily, fontWeight = FontWeight.Bold)
            Text(text = address.direccion, fontFamily = SharpSansFontFamily, color = Color.Gray, fontWeight = FontWeight.Medium)
        }
    }

}