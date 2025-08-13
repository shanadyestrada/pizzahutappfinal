package com.example.pizzahutappfinal.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pizzahutappfinal.R
import androidx.compose.ui.unit.sp
import com.example.pizzahutappfinal.components.CategoriesView
import com.example.pizzahutappfinal.model.CategoryModel
import com.example.pizzahutappfinal.ui.theme.BrixtonLeadFontFamily
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun CategoriePage(modifier: Modifier = Modifier) {

    val isLoading = remember { mutableStateOf(true) }
    val primaryColor = Color(0xFFA90A24)
    val categoryList = remember {
        mutableStateOf<List<CategoryModel>>(emptyList())
    }

    LaunchedEffect(key1 = Unit) {
        Firebase.firestore.collection("data").document("stock")
            .collection("categorias")
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val resultList = task.result.documents.mapNotNull { doc ->
                        doc.toObject(CategoryModel::class.java)
                    }
                    categoryList.value = resultList
                    isLoading.value = false
                } else {
                    isLoading.value = false
                }
            }
    }


    Column (modifier = Modifier
        .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.vector),
                contentDescription = "Logo",
                modifier = Modifier.size(64.dp)
            )

            Text(
                text = "BIENVENIDO A NUESTRO MENÚ",
                fontFamily = BrixtonLeadFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 37.sp,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading.value) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = primaryColor)
            }
        } else {
            CategoriesView(categoryList = categoryList.value)
        }
    }
}
