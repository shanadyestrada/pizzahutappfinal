package com.example.pizzahutappfinal

import android.content.Context
import android.widget.Toast
import com.example.pizzahutappfinal.model.CartItemModel
import com.example.pizzahutappfinal.model.ProductModel
import com.example.pizzahutappfinal.model.UserModel
import com.example.pizzahutappfinal.model.getTamano
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

object AppUtil {

    fun showToast(context : Context, mesagge : String) {
        Toast.makeText(context,mesagge,Toast.LENGTH_LONG).show()
    }

    fun addToCart(
        context: Context,
        productId: String,
        variationKey: String? = null,
        adicionales: List<String> = emptyList()
    ) {
        val userDoc = Firebase.firestore.collection("usuarios")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)

        userDoc.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userModel = task.result.toObject(UserModel::class.java)

                // Asegúrate de que userModel no sea nulo
                userModel?.let {
                    val currentCart = it.cartItems.toMutableList()

                    // Ordenar los adicionales para una comparación consistente
                    val sortedAdicionales = adicionales.sorted()

                    // Buscar si el ítem ya existe en el carrito
                    val existingItemIndex = currentCart.indexOfFirst { cartItem ->
                        cartItem.productoId == productId &&
                                cartItem.variaciones == variationKey &&
                                cartItem.adicionales.sorted() == sortedAdicionales
                    }

                    if (existingItemIndex != -1) {
                        // Si el ítem ya existe, incrementa la cantidad
                        val existingItem = currentCart[existingItemIndex]
                        currentCart[existingItemIndex] = existingItem.copy(cantidad = existingItem.cantidad + 1)
                    } else {
                        // Si no, añade un nuevo ítem
                        currentCart.add(
                            CartItemModel(
                                productoId = productId,
                                variaciones = variationKey,
                                cantidad = 1,
                                adicionales = sortedAdicionales
                            )
                        )
                    }

                    val updatedUserMap = mapOf("cartItems" to currentCart)

                    userDoc.update(updatedUserMap)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                showToast(context, "Producto añadido al carrito")
                            } else {
                                showToast(context, "No se pudo agregar al carrito")
                            }
                        }
                } ?: showToast(context, "Error: UserModel nulo")
            } else {
                showToast(context, "Error al obtener el carrito del usuario")
            }
        }
    }

    fun removeFromCart(
        context: Context,
        productId: String,
        variationKey: String? = null,
        removeAll: Boolean = false,
        adicionales: List<String> = emptyList()
    ) {
        val userDoc = Firebase.firestore.collection("usuarios")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)

        userDoc.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userModel = task.result.toObject(UserModel::class.java)
                userModel?.let {
                    val currentCart = it.cartItems.toMutableList()
                    val sortedAdicionales = adicionales.sorted()

                    val existingItemIndex = currentCart.indexOfFirst { cartItem ->
                        cartItem.productoId == productId &&
                                cartItem.variaciones == variationKey &&
                                cartItem.adicionales.sorted() == sortedAdicionales
                    }

                    if (existingItemIndex != -1) {
                        val existingItem = currentCart[existingItemIndex]

                        if (removeAll || existingItem.cantidad <= 1) {
                            currentCart.removeAt(existingItemIndex)
                        } else {
                            val nuevaCantidad = existingItem.cantidad - 1
                            currentCart[existingItemIndex] = existingItem.copy(cantidad = nuevaCantidad)
                        }

                        userDoc.update("cartItems", currentCart)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    showToast(context, "Producto removido del carrito")
                                } else {
                                    showToast(context, "No se pudo remover del carrito")
                                }
                            }
                    } else {
                        showToast(context, "El producto no está en el carrito")
                    }
                } ?: showToast(context, "Error: UserModel nulo")
            } else {
                showToast(context, "Error al obtener el carrito del usuario")
            }
        }
    }

    fun calculateTotal(cartItems: List<CartItemModel>, onResult: (Double) -> Unit) {
        if (cartItems.isEmpty()) {
            onResult(0.0)
            return
        }

        var total = 0.0
        val totalItems = cartItems.size
        var itemsProcessed = 0

        cartItems.forEach { cartItem ->
            val productId = cartItem.productoId
            val variationKey = cartItem.variaciones
            val adicionales = cartItem.adicionales
            val qty = cartItem.cantidad

            Firebase.firestore.collection("data").document("stock")
                .collection("productos").document(productId).get()
                .addOnSuccessListener { doc ->
                    val productModel = doc.toObject(ProductModel::class.java)

                    val price: Double
                    if (productModel?.categoria?.lowercase() == "pizzas" && variationKey != null) {
                        val (size, crust) = variationKey.split("_")
                        val tamanoModel = productModel.variaciones?.getTamano(size)

                        val priceString = tamanoModel?.let {
                            when (crust.lowercase()) {
                                "artesanal" -> it.Artesanal
                                "cheesebites" -> it.CheeseBites
                                "delgada" -> it.Delgada
                                "hutcheese" -> it.HutCheese
                                "pan" -> it.Pan
                                else -> null
                            }
                        }
                        val basePrice = priceString?.toDoubleOrNull() ?: 0.0

                        val additionalPrice = productModel.adicionales
                            .filterKeys { adicionales.contains(it) }
                            .values
                            .sumOf { it.toDoubleOrNull() ?: 0.0 }// Usa sumOf en lugar de sum()

                        price = basePrice + additionalPrice
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