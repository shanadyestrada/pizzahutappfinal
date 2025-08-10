package com.example.pizzahutappfinal.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class OrderModel(
    var orderId: String = "",
    var userId: String = "",
    var status: String = "ORDENADO",
    var cartItems: List<CartItemModel> = emptyList(),
    @ServerTimestamp
    var timestamp: Date? = null
)