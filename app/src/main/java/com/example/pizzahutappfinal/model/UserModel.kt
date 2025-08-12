package com.example.pizzahutappfinal.model

import com.google.firebase.firestore.PropertyName

data class UserModel (
        @get:PropertyName("nombre")
        @set:PropertyName("nombre")
        var nombre : String = "",

        @get:PropertyName("apellidos") // Si en Firestore es 'apellidos'
        @set:PropertyName("apellidos")
        var apellidos : String = "",

        val fechaNacimiento: String = "",

        @get:PropertyName("telefono")
        @set:PropertyName("telefono")
        var telefono : String = "",

        @get:PropertyName("email")
        @set:PropertyName("email")
        var email : String = "",

        @get:PropertyName("userId")
        @set:PropertyName("userId")
        var userId : String = "",

        val cartItems: List<CartItemModel> = emptyList()
    )

data class CartItemModel(
        val productoId: String = "",
        val variaciones: String? = null,
        val cantidad: Long = 0,
        val adicionales: List<String> = emptyList()
)

