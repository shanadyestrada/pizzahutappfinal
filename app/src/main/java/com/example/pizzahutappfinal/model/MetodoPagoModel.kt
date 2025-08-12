package com.example.pizzahutappfinal.model

import com.google.firebase.firestore.DocumentId

data class MetodoPagoModel (
    @DocumentId
    val metodoPagoId: String = "",
    val nombre: String = "",
)