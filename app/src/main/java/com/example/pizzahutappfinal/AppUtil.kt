package com.example.pizzahutappfinal

import android.content.Context
import android.widget.Toast
import com.example.pizzahutappfinal.model.ProductModel
import com.example.pizzahutappfinal.model.getTamano
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

object AppUtil {

    fun showToast(context : Context, mesagge : String) {
        Toast.makeText(context,mesagge,Toast.LENGTH_LONG).show()
    }

    fun addToCart(context: Context, productId: String, variationKey: String? = null) {
        val userDoc = Firebase.firestore.collection("usuarios")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)

        userDoc.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val currentCart = it.result.get("cartItems") as? Map<String, Long> ?: emptyMap()

                val cartKey = if (variationKey != null) {
                    "${productId}_$variationKey"
                } else {
                    productId
                }

                val currentQuantity = currentCart[cartKey] ?: 0
                val updatedQuantity = currentQuantity + 1

                val updatedCart = mapOf("cartItems.$cartKey" to updatedQuantity)

                userDoc.update(updatedCart)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            showToast(context, "Producto añadido al carrito")
                        } else {
                            showToast(context, "No se pudo agregar al carrito")
                        }
                    }
            } else {
                showToast(context, "Error al obtener el carrito del usuario")
            }
        }
    }

    fun removeFromCart(
        context: Context,
        productId: String,
        variationKey: String? = null,
        removeAll: Boolean = false
    ) {
        val userDoc = Firebase.firestore.collection("usuarios")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)

        userDoc.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val currentCart = it.result.get("cartItems") as? Map<String, Long> ?: emptyMap()

                // 1. Determinar la clave del producto en el carrito, incluyendo la variación
                val cartKey = if (variationKey != null) {
                    "${productId}_$variationKey"
                } else {
                    productId
                }

                // 2. Obtener la cantidad actual y la actualizamos
                val currentQuantity = currentCart[cartKey] ?: 0
                val updatedQuantity = currentQuantity - 1

                // 3. Crear el mapa de actualización con la clave correcta
                val updatedCart =
                    if (updatedQuantity <= 0 || removeAll) {
                        // Usa FieldValue.delete() para remover el par clave-valor si la cantidad llega a cero o si se pide remover todo.
                        mapOf("cartItems.$cartKey" to FieldValue.delete())
                    } else {
                        mapOf("cartItems.$cartKey" to updatedQuantity)
                    }

                userDoc.update(updatedCart)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            showToast(context, "Producto removido del carrito")
                        } else {
                            showToast(context, "No se pudo remover del carrito")
                        }
                    }
            }
        }
    }

    fun calculateTotal(cartItems: Map<String, Long>, onResult: (Double) -> Unit) {
        if (cartItems.isEmpty()) {
            onResult(0.0)
            return
        }

        var total = 0.0
        val totalItems = cartItems.size
        var itemsProcessed = 0

        cartItems.forEach { (cartKey, qty) ->
            val parts = cartKey.split("_")
            val productId = parts[0]
            val variationKey = if (parts.size > 1) parts.subList(1, parts.size).joinToString("_") else null

            Firebase.firestore.collection("data").document("stock")
                .collection("productos").document(productId).get()
                .addOnSuccessListener { doc ->
                    val productModel = doc.toObject(ProductModel::class.java)

                    val price: Double
                    if (productModel?.categoria == "pizzas" && variationKey != null) {
                        val (size, crust) = variationKey.split("_")
                        val tamanoModel = productModel.variaciones?.getTamano(size)

                        val priceString = tamanoModel?.let {
                            when (crust) {
                                "Artesanal" -> it.Artesanal
                                "CheeseBites" -> it.CheeseBites
                                "Delgada" -> it.Delgada
                                "HutCheese" -> it.HutCheese
                                "Pan" -> it.Pan
                                else -> null
                            }
                        }
                        price = priceString?.toDoubleOrNull() ?: 0.0
                    } else {
                        val priceString = productModel?.precio
                        price = priceString?.toDoubleOrNull() ?: 0.0
                    }

                    total += price * qty
                    itemsProcessed++

                    if (itemsProcessed == totalItems) {
                        onResult(total)
                    }
                }
                .addOnFailureListener {
                    itemsProcessed++
                    if (itemsProcessed == totalItems) {
                        onResult(total)
                    }
                }
        }
    }
}