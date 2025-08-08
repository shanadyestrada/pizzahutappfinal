package com.example.pizzahutappfinal.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.pizzahutappfinal.R
import com.example.pizzahutappfinal.ui.theme.BrixtonLeadFontFamily
import com.example.pizzahutappfinal.ui.theme.SharpSansFontFamily

@Composable
fun AuthScreen(modifier: Modifier = Modifier, navController: NavHostController) {

    val primaryColor = Color(0xFFA90A24)

    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Image(painter = painterResource(id = R.drawable.banner),
            contentDescription = "Banner",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text( text = "Más pizzas.., más momentos..!",
            style = TextStyle(
                fontSize = 45.sp,
                fontFamily = BrixtonLeadFontFamily,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(35.dp))

        // BOTÓN LOGIN
        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor
            ),
            shape = RoundedCornerShape(7.dp)
        ) {
            Text(text = "INICIAR SESIÓN",
                fontSize = 19.sp,
                fontFamily = SharpSansFontFamily,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // BOTÓN REGISTRARSE
        Button(
            onClick = { navController.navigate("signup") },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = primaryColor
            ),
            border = BorderStroke(2.dp, primaryColor),
            shape = RoundedCornerShape(7.dp)
        ) {
            Text(text = "REGISTRARSE",
                fontSize = 19.sp,
                fontFamily = SharpSansFontFamily,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )
        }
    }

}