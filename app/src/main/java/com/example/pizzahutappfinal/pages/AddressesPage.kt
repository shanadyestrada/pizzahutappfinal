package com.example.pizzahutappfinal.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var showAddAddressDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
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
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                if (isLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color(0xFFA90A24))
                    }
                } else if (addresses.isEmpty()) {
                    Text("No tienes direcciones guardadas.")
                    Button(onClick = { showAddAddressDialog = true }) {
                        Text("Agregar Dirección")
                    }
                } else {
                    addresses.forEachIndexed { index, address ->
                        AddressItem(
                            address = address,
                            index = index,
                            onDelete = {
                                viewModel.deleteAddress(address.direccionId)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
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
                            Button(onClick = { showAddAddressDialog = true }) {
                                Text("Agregar")
                            }
                        }
                    }
                }
            }
        }
    }
    if (showAddAddressDialog) {
        AddAddressDialog(
            onDismissRequest = { showAddAddressDialog = false },
            onAddAddress = { newAddress ->
                // ✅ Llama a la función del ViewModel para guardar la dirección
                viewModel.addAddress(newAddress)
                showAddAddressDialog = false
            }
        )
    }
}

@Composable
fun AddressItem(address: DireccionModel, index: Int, onDelete: () -> Unit) {
    Column(
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = address.nombre,
                    fontFamily = SharpSansFontFamily,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = address.direccion,
                    fontFamily = SharpSansFontFamily,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
            // ✅ Icono de borrado
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Borrar dirección",
                    tint = Color(0xFFA90A24)
                )
            }
        }
    }
}

        @Composable
fun AddAddressDialog(
    onDismissRequest: () -> Unit,
    onAddAddress: (DireccionModel) -> Unit
    ){
    var addressName by remember { mutableStateOf("") }
    var fullAddress by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Agregar nueva dirección") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = addressName,
                    onValueChange = { addressName = it },
                    label = { Text("Nombre de la dirección") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fullAddress,
                    onValueChange = { fullAddress = it },
                    label = { Text("Dirección completa") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (addressName.isNotBlank() && fullAddress.isNotBlank()) {
                        val newAddress = DireccionModel(direccionId = "", nombre = addressName, direccion = fullAddress)
                        onAddAddress(newAddress)
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}