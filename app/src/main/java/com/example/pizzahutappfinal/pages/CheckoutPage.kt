package com.example.pizzahutappfinal.pages

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pizzahutappfinal.AppUtil
import com.example.pizzahutappfinal.AppUtil.showToast
import com.example.pizzahutappfinal.GlobalNavigation
import com.example.pizzahutappfinal.R
import com.example.pizzahutappfinal.components.CartItemView
import com.example.pizzahutappfinal.components.CheckoutViewModel
import com.example.pizzahutappfinal.model.DireccionModel
import com.example.pizzahutappfinal.model.LocalModel
import com.example.pizzahutappfinal.model.MetodoPagoModel
import com.example.pizzahutappfinal.ui.theme.BrixtonLeadFontFamily
import com.example.pizzahutappfinal.ui.theme.SharpSansFontFamily
import com.example.pizzahutappfinal.viewmodel.ComprobanteViewModel
import com.example.pizzahutappfinal.viewmodel.LocalViewModel
import com.example.pizzahutappfinal.viewmodel.PaymentViewModel

enum class DeliveryOption {
    DELIVERY, RECOJO_EN_TIENDA
}


@Composable
fun CheckoutPage(modifier: Modifier = Modifier, navController: NavController,
                 localViewModel: LocalViewModel = viewModel(),
                 checkoutViewModel: CheckoutViewModel = viewModel()
) {
    val primaryColor = Color(0xFFA90A24)

    val context = LocalContext.current
    val totalPrice = checkoutViewModel.totalPrice.observeAsState(initial = 0.0).value
    val userProfile = checkoutViewModel.userProfile.observeAsState().value
    val direcciones = checkoutViewModel.direcciones.observeAsState(initial = emptyList()).value


    var nombre by remember(userProfile) { mutableStateOf(userProfile?.nombre.orEmpty()) }
    var apellidos by remember(userProfile) { mutableStateOf(userProfile?.apellidos.orEmpty()) }
    var telefono by remember(userProfile) { mutableStateOf(userProfile?.telefono.orEmpty()) }
    var correo by remember(userProfile) { mutableStateOf(userProfile?.email.orEmpty()) }

    val locales = localViewModel.locales.observeAsState(initial = emptyList()).value
    val isLocalesLoading = localViewModel.isLoading.observeAsState(initial = true).value

    var selectedDeliveryOption by remember { mutableStateOf(DeliveryOption.DELIVERY) }
    var selectedLocal by remember { mutableStateOf<LocalModel?>(null) }
    var selectedDireccion by remember { mutableStateOf<DireccionModel?>(null) }

    // Estados para los campos de nueva dirección
    var nuevaDireccionNombre by remember { mutableStateOf("") }
    var nuevaDireccionCalle by remember { mutableStateOf("") }

    // La lógica para decidir si mostrar los campos de agregar dirección
    val shouldShowAddressForm = remember(direcciones.size) {
        direcciones.size < 2 && (direcciones.isEmpty() || selectedDireccion == null)
    }

    val paymentViewModel: PaymentViewModel = viewModel()
    val metodosDePago = paymentViewModel.metodosDePago.observeAsState(initial = emptyList()).value
    var selectedMetodoPagoId by remember { mutableStateOf<String?>(null) }
    var selectedMetodoPago by remember { mutableStateOf<MetodoPagoModel?>(null) }

    val comprobanteViewModel: ComprobanteViewModel = viewModel()
    val tiposComprobante by comprobanteViewModel.tiposComprobante.observeAsState(initial = emptyList())

    var isBoletaElectronicaChecked by remember { mutableStateOf(false) }
    var isFacturaChecked by remember { mutableStateOf(false) }

    var cardName by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }

    // 🆕 Declara los estados para los campos de la factura
    var facturaRuc by remember { mutableStateOf("") }
    var facturaRazonSocial by remember { mutableStateOf("") }

    val selectedComprobante by remember {
        derivedStateOf {
            when {
                isBoletaElectronicaChecked -> tiposComprobante.firstOrNull { it.nombre == "Boleta Electrónica" }
                isFacturaChecked -> tiposComprobante.firstOrNull { it.nombre == "Factura" }
                else -> tiposComprobante.firstOrNull { it.nombre == "Boleta" } // Opción por defecto
            }
        }
    }

    var acceptsTerms by remember { mutableStateOf(false) }


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
                text = "PROCEDER AL PAGO!",
                fontFamily = BrixtonLeadFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 38.sp,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

            // MOSTRAR DATOS DEL USUARIO
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "¿Quién nos recibirá?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SharpSansFontFamily,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = nombre,
                                fontFamily = SharpSansFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                color = Color(0xFF858383)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = apellidos,
                                fontFamily = SharpSansFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                color = Color(0xFF858383)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = telefono,
                                fontFamily = SharpSansFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                color = Color(0xFF858383)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = correo,
                                fontFamily = SharpSansFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                color = Color(0xFF858383)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // MOSTRAR MODALIDAD DEL PEDIDO
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "¿Cómo te entregaremos el pedido?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SharpSansFontFamily,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                DeliveryAndPickupOptions(
                    selectedDeliveryOption = selectedDeliveryOption,
                    onOptionSelected = { selectedDeliveryOption = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedDeliveryOption == DeliveryOption.RECOJO_EN_TIENDA) {
                    RecojoEnTiendaUI(
                        locales = locales,
                        isLocalesLoading = isLocalesLoading,
                        selectedLocal = selectedLocal,
                        onLocalSelected = { selectedLocal = it }
                    )
                } else {
                    DeliveryUI(
                        direcciones = direcciones,
                        selectedDireccion = selectedDireccion,
                        onDireccionSelected = { selectedDireccion = it },
                        nuevaDireccionNombre = nuevaDireccionNombre,
                        onNuevaDireccionNombreChange = { nuevaDireccionNombre = it },
                        nuevaDireccionCalle = nuevaDireccionCalle,
                        onNuevaDireccionCalleChange = { nuevaDireccionCalle = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // MOSTRAR MÉTODO DE PAGO
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "¿Cómo te gustaría pagar?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SharpSansFontFamily,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (metodosDePago.isNotEmpty()) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(8.dp)
                            )
                            .padding(vertical = 8.dp)
                            .selectableGroup()
                    ) {
                        metodosDePago.forEach { metodo ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .selectable(
                                        selected = (metodo.metodoPagoId == selectedMetodoPagoId),
                                        onClick = {
                                            selectedMetodoPagoId = metodo.metodoPagoId
                                            selectedMetodoPago = metodo
                                        },
                                        role = Role.RadioButton
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (metodo.metodoPagoId == selectedMetodoPagoId),
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Color(0xFFA90A24),
                                        unselectedColor = Color(0xFFA90A24)
                                    )
                                )
                                Text(
                                    text = metodo.nombre,
                                    fontFamily = SharpSansFontFamily,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 14.dp)
                                )
                            }
                        }
                        val isPagoEnLineaSelected = selectedMetodoPago?.nombre == "Pago en Línea"
                        if (isPagoEnLineaSelected) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(modifier = Modifier.padding(horizontal = 16.dp)
                                .background(Color.LightGray.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp))) {

                                CardFields(
                                    cardName = cardName,
                                    onCardNameChange = { cardName = it },
                                    cardNumber = cardNumber,
                                    onCardNumberChange = { cardNumber = it },
                                    cardExpiry = cardExpiry,
                                    onCardExpiryChange = { cardExpiry = it },
                                    cardCvv = cardCvv,
                                    onCardCvvChange = { cardCvv = it }
                                )
                            }
                        }
                    }
                }
            }

            // MOSTRAR TIPO DE COMPORBANTE
            Spacer(modifier = Modifier.height(20.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Antes de Pagar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SharpSansFontFamily,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Sección de Términos y condiciones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 40.dp) // ✅ Se ajusta la altura mínima
                        .toggleable(
                            value = acceptsTerms,
                            onValueChange = { acceptsTerms = it },
                            role = Role.Checkbox
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = acceptsTerms,
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(
                            checkmarkColor = Color.White,
                            uncheckedColor = Color(0xFFC7011A),
                            checkedColor = Color(0xFFC7011A)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Estoy de acuerdo con las Condiciones de uso y Política de Privacidad y entiendo que mi información " +
                            "se usará como describe en este aplicativo y en Pizza Hut",
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            fontFamily = SharpSansFontFamily,
                            lineHeight = 15.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Sección de Boleta Electrónica
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 40.dp)
                        .toggleable(
                            value = isBoletaElectronicaChecked,
                            onValueChange = {
                                isBoletaElectronicaChecked = it
                                if (it) isFacturaChecked = false
                            },
                            role = Role.Checkbox
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isBoletaElectronicaChecked,
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(
                            checkmarkColor = Color.White,
                            uncheckedColor = Color(0xFFC7011A),
                            checkedColor = Color(0xFFC7011A)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "¿Desea acumular puntos Bonnus y Boleta Electrónica?",
                            fontSize = 14.sp,
                            fontFamily = SharpSansFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 14.sp
                        )
                        Text(
                            text = "Se usará la información anteriormente registrado en su cuenta",
                            fontSize = 11.sp,
                            fontFamily = SharpSansFontFamily,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Sección de Factura
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 40.dp)
                        .toggleable(
                            value = isFacturaChecked,
                            onValueChange = {
                                isFacturaChecked = it
                                if (it) isBoletaElectronicaChecked = false
                            },
                            role = Role.Checkbox
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isFacturaChecked,
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(
                            checkmarkColor = Color.White,
                            uncheckedColor = Color(0xFFC7011A),
                            checkedColor = Color(0xFFC7011A)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Necesito Factura",
                        fontSize = 14.sp,
                        fontFamily = SharpSansFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (isFacturaChecked) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        FacturaFields(
                            ruc = facturaRuc,
                            onRucChange = { facturaRuc = it },
                            razonSocial = facturaRazonSocial,
                            onRazonSocialChange = { facturaRazonSocial = it }
                        )
                    }
                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            // Usa esta sección justo antes de tu Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total:",
                        fontFamily = SharpSansFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "S/ ${"%.2f".format(totalPrice)}",
                        fontFamily = SharpSansFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Color(0xFFC7011A)
                    )
                }
                Button(
                    onClick = {
                        if (!acceptsTerms) {
                            showToast(context, "Por favor, acepte los términos y condiciones.")
                            return@Button
                        }

                        if (selectedMetodoPago == null) {
                            showToast(context, "Por favor, seleccione un método de pago.")
                            return@Button
                        }

                        val isPagoEnLineaSelected = selectedMetodoPago?.nombre == "Pago en Línea"
                        if (isPagoEnLineaSelected) {
                            if (cardName.isEmpty() || cardNumber.isEmpty() || cardExpiry.isEmpty() || cardCvv.isEmpty()) {
                                showToast(context, "Por favor, complete todos los campos de su tarjeta.")
                                return@Button
                            }
                        }

                        // 🆕 4. Validar campos de factura si está seleccionado
                        if (isFacturaChecked) {
                            if (facturaRuc.isEmpty() || facturaRazonSocial.isEmpty()) {
                                showToast(context, "Por favor, complete los campos de RUC y Razón Social.")
                                return@Button
                            }
                        }

                        when (selectedDeliveryOption) {
                            DeliveryOption.RECOJO_EN_TIENDA -> {
                                if (selectedLocal != null) {
                                    AppUtil.saveOrder(
                                        context,
                                        navController,
                                        AppUtil.OpcionDeEntrega.Recojo(selectedLocal!!),
                                        selectedMetodoPago!!,
                                        selectedComprobante
                                    )
                                } else {
                                    showToast(context, "Por favor, seleccione un local de recojo.")
                                }
                            }
                            DeliveryOption.DELIVERY -> {
                                if (shouldShowAddressForm && nuevaDireccionCalle.isNotEmpty() && nuevaDireccionNombre.isNotEmpty()) {
                                    val newAddress = DireccionModel(nombre = nuevaDireccionNombre, direccion = nuevaDireccionCalle)
                                    AppUtil.saveOrderWithNewAddress(
                                        context,
                                        navController,
                                        newAddress,
                                        selectedMetodoPago!!,
                                        selectedComprobante
                                    )
                                } else if (selectedDireccion != null) {
                                    AppUtil.saveOrder(
                                        context,
                                        navController,
                                        AppUtil.OpcionDeEntrega.Delivery(selectedDireccion!!),
                                        selectedMetodoPago!!,
                                        selectedComprobante
                                    )
                                } else {
                                    showToast(context, "Por favor, seleccione una dirección o complete los campos.")
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)

                ) {
                    Text(text = "PAGAR",
                        modifier = Modifier.padding(5.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SharpSansFontFamily,
                        letterSpacing = 2.sp)

                }
            }
        }

    }
}

@Composable
fun RecojoEnTiendaUI(locales: List<LocalModel>, isLocalesLoading: Boolean, selectedLocal: LocalModel?,
                     onLocalSelected: (LocalModel) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        if (isLocalesLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .padding(vertical = 8.dp)
                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedLocal != null) {
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
                                    text = selectedLocal.nombre,
                                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                                    fontFamily = SharpSansFontFamily
                                )
                                Text(
                                    text = selectedLocal.direccion, fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium, color = Color.Gray,
                                    fontFamily = SharpSansFontFamily, lineHeight = 14.sp
                                )
                            }
                        }
                    } else {
                        Text(text = "Seleccione un local de recojo",
                            fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold,
                            fontFamily = SharpSansFontFamily)
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
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Icono de local",
                                        tint = Color(0xFFA90A24),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(local.nombre, fontWeight = FontWeight.Bold, fontFamily = SharpSansFontFamily)
                                        Text(local.direccion, color = Color.Gray, fontFamily = SharpSansFontFamily,
                                            fontWeight = FontWeight.Medium)
                                    }
                                }
                            },
                            onClick = { onLocalSelected(local); expanded = false }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryUI(direcciones: List<DireccionModel>, selectedDireccion: DireccionModel?,
               onDireccionSelected: (DireccionModel?) -> Unit, nuevaDireccionNombre: String,
               onNuevaDireccionNombreChange: (String) -> Unit, nuevaDireccionCalle: String,
               onNuevaDireccionCalleChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showAddressForm by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        when {
            // Caso 1: Tiene 2 direcciones
            direcciones.size == 2 -> {
                Text(text = "Seleccione una dirección de entrega: ",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold, fontFamily = SharpSansFontFamily,
                    modifier = Modifier.padding(bottom = 4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                        .padding(vertical = 8.dp)
                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selectedDireccion != null) {
                            Column {
                                Text(
                                    text = selectedDireccion.nombre, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                                    fontFamily = SharpSansFontFamily
                                )
                                Text(
                                    text = selectedDireccion.direccion, fontSize = 12.sp, color = Color.Gray,
                                    fontWeight = FontWeight.Medium, fontFamily = SharpSansFontFamily
                                )
                            }
                        } else {
                            Text(
                                text = "Seleccione una dirección", fontSize = 14.sp, color = Color.Gray,
                                fontWeight = FontWeight.SemiBold, fontFamily = SharpSansFontFamily
                            )
                        }

                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Expandir")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        direcciones.forEach { dir ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = dir.nombre, fontWeight = FontWeight.Bold, fontFamily = SharpSansFontFamily
                                        )
                                        Text(
                                            text = dir.direccion, color = Color.Gray, fontFamily = SharpSansFontFamily,
                                            fontWeight = FontWeight.Medium
                                        )
                                    } },
                                onClick = { onDireccionSelected(dir); expanded = false }
                            )
                        }
                    }
                }
            }

            // Caso 2: Tiene 1 dirección
            direcciones.size == 1 -> {
                Text( text = "Seleccione o agregue una dirección:",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold, fontFamily = SharpSansFontFamily,
                    modifier = Modifier.padding(bottom = 4.dp))
                if (!showAddressForm) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                            .padding(vertical = 8.dp)
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (selectedDireccion != null) {
                                Column {
                                    Text(
                                        text = selectedDireccion.nombre, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                                        fontFamily = SharpSansFontFamily
                                    )
                                    Text(
                                        text = selectedDireccion.direccion, fontSize = 12.sp, color = Color.Gray,
                                        fontWeight = FontWeight.Medium, fontFamily = SharpSansFontFamily
                                    )
                                }
                            } else {
                                Text(
                                    text = "Seleccione una dirección", fontSize = 14.sp, color = Color.Gray,
                                    fontWeight = FontWeight.SemiBold, fontFamily = SharpSansFontFamily
                                )
                            }

                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Expandir")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            direcciones.forEach { dir ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = dir.nombre, fontWeight = FontWeight.Bold, fontFamily = SharpSansFontFamily
                                            )
                                            Text(text = dir.direccion, color = Color.Gray, fontFamily = SharpSansFontFamily,
                                                fontWeight = FontWeight.Medium
                                            )
                                        } },
                                    onClick = { onDireccionSelected(dir); expanded = false }
                                )
                            }

                            DropdownMenuItem(
                                text = { Text("Agregar nueva dirección",
                                    fontFamily = SharpSansFontFamily, fontWeight = FontWeight.SemiBold, color = Color.Blue) },
                                onClick = {
                                    showAddressForm = true
                                    onDireccionSelected(null)
                                    expanded = false
                                }
                            )
                        }
                    }
                } else {
                    AddressForm(
                        nombre = nuevaDireccionNombre, onNombreChange = onNuevaDireccionNombreChange,
                        calle = nuevaDireccionCalle, onCalleChange = onNuevaDireccionCalleChange,
                        onCancel = { showAddressForm = false }
                    )
                }
            }

            // Caso 3: No tiene direcciones
            direcciones.isEmpty() -> {
                Text(text = "Agregue una dirección para su pedido:",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold, fontFamily = SharpSansFontFamily,
                    modifier = Modifier.padding(bottom = 4.dp))
                AddressForm (nombre = nuevaDireccionNombre, onNombreChange = onNuevaDireccionNombreChange,
                    calle = nuevaDireccionCalle, onCalleChange = onNuevaDireccionCalleChange, onCancel = { }
                )
            }
        }
    }
}

@Composable
fun AddressForm(nombre: String, onNombreChange: (String) -> Unit, calle: String,
                onCalleChange: (String) -> Unit, onCancel: () -> Unit
) {
    Column {
        OutlinedTextField(
            value = nombre,
            onValueChange = onNombreChange,
            label = { Text(
                text = "Nombre de la dirección (ej: Casa, Trabajo)",
                fontWeight = FontWeight.SemiBold, fontFamily = SharpSansFontFamily, fontSize = 13.sp, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = calle,
            onValueChange = onCalleChange,
            label = { Text(
                text = "Dirección completa",
                fontWeight = FontWeight.SemiBold, fontFamily = SharpSansFontFamily, fontSize = 13.sp, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun DeliveryAndPickupOptions(selectedDeliveryOption: DeliveryOption, onOptionSelected: (DeliveryOption) -> Unit
) {
    val deliveryCardColor by animateColorAsState(
        if (selectedDeliveryOption == DeliveryOption.DELIVERY) Color(0xFFC7011A) else Color.LightGray
    )
    val pickupCardColor by animateColorAsState(
        if (selectedDeliveryOption == DeliveryOption.RECOJO_EN_TIENDA) Color(0xFFC7011A) else Color.LightGray
    )

    val deliveryContentColor by animateColorAsState(
        if (selectedDeliveryOption == DeliveryOption.DELIVERY) Color.White else Color.Black
    )
    val pickupContentColor by animateColorAsState(
        if (selectedDeliveryOption == DeliveryOption.RECOJO_EN_TIENDA) Color.White else Color.Black
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onOptionSelected(DeliveryOption.DELIVERY) },
            colors = CardDefaults.cardColors(containerColor = deliveryCardColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Entrega",
                    tint = deliveryContentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Entrega",
                    color = deliveryContentColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Card(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onOptionSelected(DeliveryOption.RECOJO_EN_TIENDA) },
            colors = CardDefaults.cardColors(containerColor = pickupCardColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Recojo en Tienda",
                    tint = pickupContentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Recojo en Tienda",
                    color = pickupContentColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun CardFields(
    // 🆕 Nombres de parámetros corregidos para que coincidan con la llamada
    cardName: String,
    onCardNameChange: (String) -> Unit,
    cardNumber: String,
    onCardNumberChange: (String) -> Unit,
    cardExpiry: String,
    onCardExpiryChange: (String) -> Unit,
    cardCvv: String,
    onCardCvvChange: (String) -> Unit
) {

    val primaryColor = Color(0xFFA90A24)
    Column(modifier = Modifier.padding(12.dp)
    ) {
        OutlinedTextField(
            // 🆕 Se usa el nuevo nombre del parámetro
            value = cardName,
            onValueChange = onCardNameChange,
            label = { Text(text = "Titular de la Tarjeta",
                fontWeight = FontWeight.SemiBold, fontFamily = SharpSansFontFamily,
                fontSize = 12.sp, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = primaryColor,
                unfocusedLabelColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            // 🆕 Se usa el nuevo nombre del parámetro
            value = cardNumber,
            onValueChange = onCardNumberChange,
            label = { Text(text = "Número de Tarjeta",
                fontWeight = FontWeight.SemiBold, fontFamily = SharpSansFontFamily,
                fontSize = 12.sp, color = Color.Gray) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = primaryColor,
                unfocusedLabelColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                // 🆕 Se usa el nuevo nombre del parámetro
                value = cardExpiry,
                onValueChange = onCardExpiryChange,
                label = { Text(text = "MMAA",
                    fontWeight = FontWeight.SemiBold, fontFamily = SharpSansFontFamily,
                    fontSize = 12.sp, color = Color.Gray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = primaryColor,
                    unfocusedLabelColor = Color.Gray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = Color.Black
                )
            )
            OutlinedTextField(
                // 🆕 El nombre del parámetro ya era correcto, pero se mantiene la consistencia
                value = cardCvv,
                onValueChange = onCardCvvChange,
                label = { Text(text = "CVV",
                    fontWeight = FontWeight.SemiBold, fontFamily = SharpSansFontFamily,
                    fontSize = 13.sp, color = Color.Gray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = primaryColor,
                    unfocusedLabelColor = Color.Gray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = Color.Black
                )
            )
        }
    }
}

@Composable
fun FacturaFields(
    ruc: String,
    onRucChange: (String) -> Unit,
    razonSocial: String,
    onRazonSocialChange: (String) -> Unit
) {
    val primaryColor = Color(0xFFA90A24)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = ruc,
            onValueChange = onRucChange, // 🆕 Se usa el callback
            label = {
                Text(
                    text = "RUC",
                    fontWeight = FontWeight.SemiBold, fontFamily = SharpSansFontFamily,
                    fontSize = 12.sp, color = Color.Gray
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = primaryColor,
                unfocusedLabelColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = razonSocial,
            onValueChange = onRazonSocialChange, // 🆕 Se usa el callback
            label = {
                Text(
                    text = "Razón Social",
                    fontWeight = FontWeight.SemiBold, fontFamily = SharpSansFontFamily,
                    fontSize = 12.sp, color = Color.Gray
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = primaryColor,
                unfocusedLabelColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color.Black
            )
        )
    }
}