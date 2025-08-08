package com.example.pizzahutappfinal.model

data class UserModel (
        val nombre : String = "",
        val apellidos : String = "",
        val fechaNacimiento: String = "",
        val telefono : String = "",
        val email : String = "",
        val userId : String = "",
        val cartItems : Map<String, Long> = emptyMap()
    )
