package com.example.pizzahutappfinal.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahutappfinal.GlobalNavigation
import com.example.pizzahutappfinal.components.ProductItemView
import com.example.pizzahutappfinal.model.ProductModel
import com.example.pizzahutappfinal.R
import com.example.pizzahutappfinal.ui.theme.BrixtonLeadFontFamily
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun CategoryProductsPage(modifier: Modifier = Modifier, categoryId : String) {
    Column {
        val productList = remember {
            mutableStateOf<List<ProductModel>>(emptyList())
        }
        val categoryName = remember { mutableStateOf("") }

        LaunchedEffect (key1 = Unit) {
            Firebase.firestore.collection("data").document("stock")
                .collection("productos")
                .whereEqualTo("categoria", categoryId )
                .get().addOnCompleteListener() { task ->
                    if(task.isSuccessful) {
                        val resultList = task.result.documents.mapNotNull { doc ->
                            doc.toObject(ProductModel :: class.java)
                        }
                        productList.value = resultList
                    }
                }
        }

        // Obtener nombre de la categoría
        LaunchedEffect(Unit) {
            Firebase.firestore.collection("data").document("stock")
                .collection("categorias")
                .document(categoryId)
                .get().addOnSuccessListener { doc ->
                    doc?.let {
                        val name = it.getString("nombre") ?: ""
                        categoryName.value = name
                    }
                }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding() // ajusta automáticamente
                .background(Color(0xFFAF0014))
                .padding(horizontal = 16.dp, vertical = 14.dp), // controla el espacio interno
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
                text = categoryName.value,
                fontFamily = BrixtonLeadFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 38.sp,
            )
        }

        Column (modifier = Modifier.fillMaxSize()){

            LazyColumn (
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                items(productList.value) { item ->
                    ProductItemView(
                        product = item,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }


    }

}