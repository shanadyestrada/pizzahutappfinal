package com.example.pizzahutappfinal.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pizzahutappfinal.GlobalNavigation
import com.example.pizzahutappfinal.model.CategoryModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun CategoriesView(categoryList: List<CategoryModel>, modifier: Modifier = Modifier) {
    val categoryList = remember {
        mutableStateOf<List<CategoryModel>>(emptyList())
    }

    LaunchedEffect(key1 = Unit) {
        Firebase.firestore.collection("data").document("stock")
            .collection("categorias")
            .get().addOnCompleteListener() { task ->
                if(task.isSuccessful) {
                    val resultList = task.result.documents.mapNotNull { doc ->
                        doc.toObject(CategoryModel :: class.java)
                    }
                    categoryList.value = resultList
                } else {
            }
        }
    }

    LazyColumn (
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(
            start = 18.dp,
            end = 18.dp,
            top = 20.dp,
            bottom = 80.dp
        ),
        modifier = modifier
            .fillMaxWidth()
    ) {
        items(categoryList.value) { item ->
            CategoItem(category = item, modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
            )
        }
    }
}

@Composable
fun CategoItem(category : CategoryModel, modifier: Modifier = Modifier) {
    Card (
        modifier = modifier.clickable {
            GlobalNavigation.navController.navigate("category-products/${category.id}")
        },
        shape = RoundedCornerShape(7.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ){
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ){
            AsyncImage(
                model = category.imageUrl,
                contentDescription = category.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.nombre,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}