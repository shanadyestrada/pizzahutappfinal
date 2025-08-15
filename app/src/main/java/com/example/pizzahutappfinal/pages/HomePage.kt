package com.example.pizzahutappfinal.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pizzahutappfinal.GlobalNavigation
import com.example.pizzahutappfinal.R
import com.example.pizzahutappfinal.components.BannerView
import com.example.pizzahutappfinal.components.ProductItemView
import com.example.pizzahutappfinal.model.ProductModel
import com.example.pizzahutappfinal.ui.theme.BrixtonLeadFontFamily
import com.example.pizzahutappfinal.viewmodel.HomeViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel() // Obtiene la instancia del ViewModel
) {
    val products by homeViewModel.products.collectAsState()

    val featuredProducts = products.groupBy { it.categoria }
        .mapValues { (_, categoryProducts) -> categoryProducts.first() }
        .values.toList()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        BannerView(modifier = Modifier.height(280.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // ... (Tu código para el logo y el título)
            Image(
                painter = painterResource(id = R.drawable.vector),
                contentDescription = "Logo",
                modifier = Modifier.size(61.dp)
            )

            // Título
            Text(
                text = "PRUEBA NUESTROS PRODUCTOS!",
                fontFamily = BrixtonLeadFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 33.sp,
            )
            // Aquí se listan las cards de productos destacados
            LazyRow (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre las tarjetas
                contentPadding = PaddingValues(horizontal = 0.dp) // No es necesario el padding horizontal aquí ya que lo tenemos en el Column
            ) {
                items(featuredProducts) { product ->
                    ProductItemView(product = product)
                }
            }

            // ... (El resto de tu código, incluyendo el botón)
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate("categories_screen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding( bottom = 72.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFAF0014)
                )
            ) {
                Text(
                    text = "VER TODAS LAS CATEGORÍAS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }


            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
