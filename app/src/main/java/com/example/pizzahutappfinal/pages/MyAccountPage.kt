package com.example.pizzahutappfinal.pages

import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.pizzahutappfinal.GlobalNavigation.navController
import com.example.pizzahutappfinal.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.example.pizzahutappfinal.ui.theme.BrixtonLeadFontFamily
import com.example.pizzahutappfinal.ui.theme.SharpSansFontFamily

object Routes {
    const val MY_ACCOUNT = "my_account"
    const val PROFILE_DETAILS = "profile_details"
    const val MY_ADDRESSES = "my_addresses"
    const val MY_ORDERS = "my_orders"
    const val LOGIN = "login" // Assuming a login screen for logout redirection
}

@Composable
fun MyAccountPage(
    modifier: Modifier = Modifier, navController: NavController) {

    Column (modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 32.dp, vertical = 94.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){
        Image(
            painter = painterResource(id = R.drawable.vector),
            contentDescription = "Logo",
            modifier = Modifier.size(88.dp)
        )
        Text(
            text = "MI CUENTA",
            fontFamily = BrixtonLeadFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 45.sp,
        )

        Spacer(modifier = Modifier.height(32.dp))

        AccountActionButton(
            text = "Mi información",
            onClick = { navController.navigate("profile_details") },
            backgroundColor = Color(0xFFA90A24)
        )

        Spacer(modifier = Modifier.height(8.dp))

        AccountActionButton(
            text = "Mis direcciones",
            onClick = { navController.navigate("addressesPage") },
            backgroundColor = Color(0xFFA90A24)
        )

        Spacer(modifier = Modifier.height(8.dp))

        AccountActionButton(
            text = "Ver mis pedidos",
            onClick = { navController.navigate("order_history") },
            backgroundColor = Color(0xFFA90A24)
        )

        Spacer(modifier = Modifier.height(12.dp))

        AccountActionButton(
            text = "Cerrar Sesión",
            onClick = {
                Firebase.auth.signOut()
                navController.navigate("auth") {

                    popUpTo("home") { inclusive = true }
                }
            },
            backgroundColor = Color.Black,
            textColor = Color.White
        )

    }
}

@Composable
private fun AccountActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    textColor: Color = Color.White
) {

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Text(text = text,
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            fontFamily = SharpSansFontFamily,
        )
    }
}