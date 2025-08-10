package com.example.pizzahutappfinal.model

data class UserModel (
        val nombre : String = "",
        val apellidos : String = "",
        val fechaNacimiento: String = "",
        val telefono : String = "",
        val email : String = "",
        val userId : String = "",
        val cartItems: List<CartItemModel> = emptyList()
    )

data class CartItemModel(
        val productoId: String = "",
        val variaciones: String? = null,
        val cantidad: Long = 0,
        val adicionales: List<String> = emptyList()
)

