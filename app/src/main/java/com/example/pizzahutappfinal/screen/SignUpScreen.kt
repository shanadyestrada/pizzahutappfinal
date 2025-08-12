package com.example.pizzahutappfinal.screen

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pizzahutappfinal.AppUtil
import com.example.pizzahutappfinal.R
import com.example.pizzahutappfinal.ui.theme.BrixtonLeadFontFamily
import com.example.pizzahutappfinal.ui.theme.SharpSansFontFamily
import com.example.pizzahutappfinal.viewmodel.AuthViewModel
import java.util.Calendar

@Composable
fun SignUpScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel = viewModel()) {


    val primaryColor = Color(0xFFA90A24)

    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember {
        mutableStateOf(false)
    }
    var passwordVisible by remember { mutableStateOf(false) }
    var acceptsTerms by remember { mutableStateOf(false) }
    var context = LocalContext.current
    var year: Int
    var month: Int
    var day: Int

    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
                fechaNacimiento = String.format(
                    "%02d/%02d/%d",
                    selectedDayOfMonth,
                    selectedMonth + 1,
                    selectedYear
                )
            }, year, month, day
        )
    }.apply{
        datePicker.maxDate = System.currentTimeMillis()
    }

    val scrollState = rememberScrollState()

    Column (modifier = Modifier
        .fillMaxSize()
        .padding(32.dp)
        .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Image (
            painter = painterResource(id = R.drawable.vector),
            contentDescription = "Pizza Hut Logo",
            modifier = Modifier.size(110.dp),
        )

        Text(
            text = "REGISTRATE",
            style = TextStyle(
                fontSize = 42.sp,
                textAlign = TextAlign.Center,
                fontFamily = BrixtonLeadFontFamily
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(15.dp))

        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            // Nombres
            OutlinedTextField(
                value = nombre,
                onValueChange = { newValue ->
                    if (newValue.all { it.isLetter() || it.isWhitespace() } || newValue.isBlank()) {
                        nombre = newValue
                    }
                },
                label = {
                    Text(
                        "Nombres *",
                        style = TextStyle(
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            fontFamily = SharpSansFontFamily
                        )
                    )
                },
                shape = RoundedCornerShape(7.dp),
                modifier = Modifier
                    .fillMaxWidth()
            )

            // Apellidos
            OutlinedTextField(
                value = apellidos,
                onValueChange = { newValue ->
                    if (newValue.all { it.isLetter() || it.isWhitespace() } || newValue.isBlank()) {
                        apellidos = newValue
                    }
                },
                label = { Text("Apellidos *",
                    style = TextStyle(
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        fontFamily = SharpSansFontFamily
                    )
                ) },
                shape = RoundedCornerShape(7.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Fecha de Nacimiento
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() }
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(7.dp)
                    )
            ) {
                OutlinedTextField(
                    value = fechaNacimiento,
                    onValueChange = {},
                    label = { Text("Fecha de Nacimiento *",
                        style = TextStyle(
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            fontFamily = SharpSansFontFamily
                        )
                    ) },
                    readOnly = true,
                    enabled = false,
                    shape = RoundedCornerShape(7.dp),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Seleccionar Fecha",
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color.Transparent,
                        disabledTextColor = Color.Black
                    )
                )
            }

            // Teléfono
            OutlinedTextField(
                value = telefono,
                onValueChange = { newValue ->
                    val filteredValue = newValue.filter { it.isDigit() }
                    if (filteredValue.length <= 9) {
                        telefono = filteredValue
                    }
                },
                label = { Text("Teléfono *",
                    style = TextStyle(
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        fontFamily = SharpSansFontFamily
                    )
                ) },
                shape = RoundedCornerShape(7.dp),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )

            // Correo Electrónico
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico *",
                    style = TextStyle(
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        fontFamily = SharpSansFontFamily
                    )
                ) },
                shape = RoundedCornerShape(7.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Contraseña
            OutlinedTextField(
                value = password, onValueChange = {
                password = it
            }, label = {
                Text(text = "Contraseña *",
                    style = TextStyle(
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        fontFamily = SharpSansFontFamily
                    )
                )
            },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val imagePainter = if(passwordVisible)
                        painterResource(id = R.drawable.ic_visibility)
                    else painterResource(id = R.drawable.ic_visibility_off)
                    val description =
                        if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    IconButton(onClick ={ passwordVisible = !passwordVisible}) {
                        Icon(
                            painter = imagePainter,
                            contentDescription = description,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            // Términos y condiciones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
                        uncheckedColor = primaryColor,
                        checkedColor = primaryColor
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(text = "Acepto los Términos de Uso y Política de Privacidad y entiendo que mi información será utilizada tal como se describe en este aplicativo.",
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        fontFamily = SharpSansFontFamily
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Button (
                onClick = {
                    // Validar que no haya campos vacíos
                    if (nombre.isBlank() || apellidos.isBlank() || fechaNacimiento.isBlank() ||
                        telefono.isBlank() || email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    authViewModel.signup(email = email,
                        nombre = nombre,
                        apellidos = apellidos,
                        password = password,
                        fechaNacimiento = fechaNacimiento,
                        telefono = telefono) {sucess,errorMesagge ->
                        if (sucess) {
                            isLoading = false
                            navController.navigate("home") {
                                popUpTo("auth") {
                                    inclusive = true
                                }
                            }
                        } else {
                            isLoading = false
                            AppUtil.showToast(context,errorMesagge?: "Algo salió mal..")
                        }
                    }
                },
                enabled = !isLoading && acceptsTerms && nombre.isNotBlank() && apellidos.isNotBlank() &&
                        fechaNacimiento.isNotBlank() && telefono.isNotBlank() && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(text = if (isLoading) "Creando la cuenta.." else "Registrarse", fontSize = 22.sp)
            }
        }
    }
}