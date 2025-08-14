package com.example.pizzahutappfinal.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzahutappfinal.GlobalNavigation
import com.example.pizzahutappfinal.GlobalNavigation.navController
import com.example.pizzahutappfinal.ui.theme.SharpSansFontFamily
import com.example.pizzahutappfinal.viewmodel.ProfileUiState
import com.example.pizzahutappfinal.viewmodel.ProfileViewModel
import com.example.pizzahutappfinal.R
import com.example.pizzahutappfinal.ui.theme.BrixtonLeadFontFamily

@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()) {
    val uiState by viewModel.userProfileState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 94.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        when(uiState){
            is ProfileUiState.Loading ->{
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Cargando perfil...")
            }
            is ProfileUiState.Success -> {
                val user = (uiState as ProfileUiState.Success).user
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

                Image(
                    painter = painterResource(id = R.drawable.vector),
                    contentDescription = "Logo",
                    modifier = Modifier.size(88.dp)
                )

                Text(
                    text = "Perfil del Usuario",
                    fontFamily = BrixtonLeadFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 45.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = "${user.nombre} ${user.apellidos}",
                    onValueChange = {},
                    label = { Text("Nombre Completo",
                        style = TextStyle(
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            fontFamily = SharpSansFontFamily
                        )
                    )
                            },
                    shape = RoundedCornerShape(7.dp),
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = user.fechaNacimiento,
                    onValueChange = {},
                    label = { Text("Fecha de Nacimiento" ,
                        style = TextStyle(
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            fontFamily = SharpSansFontFamily
                        )
                    )
                            },
                    readOnly = true,
                    shape = RoundedCornerShape(7.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = user.telefono,
                    onValueChange = {},
                    label = { Text("Teléfono" ,
                        style = TextStyle(
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            fontFamily = SharpSansFontFamily
                        )
                    )
                            },
                    readOnly = true,
                    shape = RoundedCornerShape(7.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = user.email,
                    onValueChange = {},
                    label = { Text("Email",
                        style = TextStyle(
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            fontFamily = SharpSansFontFamily
                        )
                    )
                            },
                    readOnly = true,
                    shape = RoundedCornerShape(7.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        navController.navigate("edit_profile")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC02128)),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Text(text = "Editar Perfil")
                }
            }
            is ProfileUiState.Error -> {
                val errorMessage = (uiState as ProfileUiState.Error).message
                Text(text = "Error: $errorMessage", color = MaterialTheme.colorScheme.error)
            }
        }
    }

}