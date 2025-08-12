package com.example.pizzahutappfinal.model

import com.google.firebase.firestore.DocumentId

data class DireccionModel(
    @DocumentId
    val direccionId: String = "",
    val nombre: String = "",
    val direccion: String = "",
)