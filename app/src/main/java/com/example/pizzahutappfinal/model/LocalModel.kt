package com.example.pizzahutappfinal.model

import com.google.firebase.firestore.DocumentId

data class LocalModel(
    @DocumentId
    val direccion: String = "",
    val localId: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val nombre: String = ""
)