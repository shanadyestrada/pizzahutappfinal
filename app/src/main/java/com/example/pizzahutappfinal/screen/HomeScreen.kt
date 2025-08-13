package com.example.pizzahutappfinal.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.pizzahutappfinal.pages.HomePage
import com.example.pizzahutappfinal.pages.LocalPage
import com.example.pizzahutappfinal.pages.MyAccountPage

import androidx.navigation.NavController
import com.example.pizzahutappfinal.R
import com.example.pizzahutappfinal.pages.CartPage
import com.example.pizzahutappfinal.pages.CategoriePage
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {

    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    val navItemList = listOf(
        NavItem("Inicio", Icons.Default.Home),
        NavItem("Menú", Icons.Default.Favorite),
        NavItem("Locales", Icons.Default.LocationOn),
        NavItem("Carrito", Icons.Default.ShoppingCart),
        NavItem("Cuenta", Icons.Default.AccountCircle)
    )

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 6.dp,
                        ambientColor = Color.Gray,
                        spotColor = Color.Gray
                    )
                    .background(Color.White),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                navItemList.forEachIndexed { index, item ->
                    val isSelected = index == selectedIndex

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isSelected) Color(0xFFAF0014) else Color.White)
                            .clickable { selectedIndex = index }
                            .padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) Color.White else Color(0xFFAF0014)
                        )
                        Text(
                            text = item.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color(0xFFAF0014)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(Color(0xFFA90A24))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo Pizza Hut",
                    modifier = Modifier.size(34.dp)
                )
            }

            ContentScreen(
                modifier = Modifier.fillMaxSize(),
                selectedIndex = selectedIndex,
                navController = navController
            )
        }
    }
}

@Composable
fun ContentScreen(modifier: Modifier = Modifier, selectedIndex : Int, navController: NavController) {
    when (selectedIndex) {
        0 -> HomePage(modifier)
        1 -> CategoriePage(modifier)
        2 -> LocalPage(modifier)
        3 -> CartPage(modifier)
        4 -> MyAccountPage(modifier, navController)
    }
}

data class NavItem (
    val label: String,
    val icon: ImageVector
)