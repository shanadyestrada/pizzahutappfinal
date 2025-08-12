package com.example.pizzahutappfinal.model

import com.google.firebase.firestore.DocumentId

data class TipoComprobanteModel (
    @DocumentId
    val tipoComprobanteId: String = "",
    val nombre: String = "",
)