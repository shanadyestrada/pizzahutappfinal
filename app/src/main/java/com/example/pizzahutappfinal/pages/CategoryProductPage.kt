package com.example.pizzahutappfinal.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategoryProductsPage(modifier: Modifier = Modifier, categoryId : String) {
    Column  (modifier = Modifier
        .fillMaxSize()
        .padding(32.dp)
        .height(18.dp)
    ) {
        Text(text = "Página para las categorias ::::: " + categoryId)
    }

}