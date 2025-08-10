package com.example.pizzahutappfinal

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.example.pizzahutappfinal.model.CartItemModel
import com.example.pizzahutappfinal.model.OrderModel
import com.example.pizzahutappfinal.model.ProductModel
import com.example.pizzahutappfinal.model.UserModel
import com.example.pizzahutappfinal.model.getTamano
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.UUID

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

    fun saveOrder(context: Context, navController: NavController) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            showToast(context, "Error: Usuario no autenticado.")
            return
        }

        val firestore = FirebaseFirestore.getInstance()
        val userDocRef = firestore.collection("usuarios").document(userId)

        userDocRef.get().addOnSuccessListener { userSnapshot ->
            val userModel = userSnapshot.toObject(UserModel::class.java)

            if (userModel != null && userModel.cartItems.isNotEmpty()) {
                // 1. Crear el modelo de la orden
                val orderId = "ORDEN-${UUID.randomUUID().toString().take(8).uppercase()}"
                val newOrder = OrderModel(
                    orderId = orderId,
                    userId = userId,
                    cartItems = userModel.cartItems,
                    status = "ORDENADO"
                )

                // 2. Guardar la orden en la colección 'orders'
                firestore.collection("orders").document(orderId).set(newOrder)
                    .addOnSuccessListener {
                        // 3. Vaciar el carrito del usuario después de guardar la orden
                        userDocRef.update("cartItems", emptyList<CartItemModel>())
                            .addOnSuccessListener {
                                showToast(context, "Pedido realizado con éxito. ID: $orderId")
                                // 🚀 Navegar a la página de la boleta
                                navController.navigate("invoicePage/$orderId")
                            }
                            .addOnFailureListener {
                                showToast(context, "Pedido guardado, pero no se pudo limpiar el carrito.")
                            }
                    }
                    .addOnFailureListener {
                        showToast(context, "Error al guardar el pedido: ${it.message}")
                    }
            } else {
                showToast(context, "El carrito está vacío.")
            }
        }.addOnFailureListener {
            showToast(context, "Error al obtener los datos del usuario.")
        }
    }

    fun getCartItems(onResult: (List<CartItemModel>) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            onResult(emptyList())
            return
        }

        FirebaseFirestore.getInstance().collection("usuarios").document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                val userModel = documentSnapshot.toObject(UserModel::class.java)
                onResult(userModel?.cartItems ?: emptyList())
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}