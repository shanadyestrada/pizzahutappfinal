package com.example.pizzahutappfinal

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.navigation.NavController
import com.example.pizzahutappfinal.model.CartItemModel
import com.example.pizzahutappfinal.model.DireccionModel
import com.example.pizzahutappfinal.model.LocalModel
import com.example.pizzahutappfinal.model.MetodoPagoModel
import com.example.pizzahutappfinal.model.OrderModel
import com.example.pizzahutappfinal.model.ProductModel
import com.example.pizzahutappfinal.model.TipoComprobanteModel
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

    sealed class OpcionDeEntrega {
        data class Recojo(val local: LocalModel) : OpcionDeEntrega()
        data class Delivery(val direccion: DireccionModel) : OpcionDeEntrega()
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
                            .sumOf { it.toDoubleOrNull() ?: 0.0 }

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


    fun saveOrderWithNewAddress(context: Context, navController: NavController,
                                newAddress: DireccionModel, metodoPago: MetodoPagoModel, selectedComprobante: TipoComprobanteModel?) {
        // 1. Guardar la nueva dirección en la subcolección del usuario
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            showToast(context, "Error: Usuario no autenticado.")
            return
        }

        val firestore = FirebaseFirestore.getInstance()
        val userDocRef = firestore.collection("usuarios").document(userId)

        userDocRef.collection("direcciones").add(newAddress)
            .addOnSuccessListener {
                // 2. Después de guardar la dirección, guardar el pedido de delivery
                saveOrder(context, navController, OpcionDeEntrega.Delivery(newAddress), metodoPago, selectedComprobante)
            }
            .addOnFailureListener {
                showToast(context, "Error al guardar la nueva dirección: ${it.message}")
            }
    }


    fun saveOrder(context: Context, navController: NavController, opcionDeEntrega: OpcionDeEntrega,
                  metodoPago: MetodoPagoModel, selectedComprobante: TipoComprobanteModel?) {
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

                calculateTotal(cartItems = userModel.cartItems) { calculatedTotal ->

                    val orderId = "ORDEN-${UUID.randomUUID().toString().take(8).uppercase()}"
                    val tipoComprobanteFinal = selectedComprobante ?: TipoComprobanteModel(
                        nombre = "Boleta"
                    )

                    val newOrder = OrderModel(
                        orderId = orderId,
                        userId = userId,
                        cartItems = userModel.cartItems,
                        status = "ORDENADO",
                        localDeRecojo = (opcionDeEntrega as? OpcionDeEntrega.Recojo)?.local,
                        deliveryDireccion = (opcionDeEntrega as? OpcionDeEntrega.Delivery)?.direccion,
                        metodoPago = metodoPago,
                        tipoComprobante = tipoComprobanteFinal,
                        orderTotal = calculatedTotal
                    )

                    firestore.collection("orders").document(orderId).set(newOrder)
                        .addOnSuccessListener {
                            userDocRef.update("cartItems", emptyList<CartItemModel>())
                                .addOnSuccessListener {
                                    showToast(context, "Pedido realizado con éxito.")
                                    navController.navigate("invoicePage/$orderId") {
                                        popUpTo("home") { inclusive = false }
                                    }
                                }
                                .addOnFailureListener {
                                    showToast(
                                        context,
                                        "Pedido guardado, pero no se pudo limpiar el carrito."
                                    )
                                }
                        }
                        .addOnFailureListener {
                            showToast(context, "Error al guardar el pedido: ${it.message}")
                        }
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
    object CrustImages {
        val images = mapOf(
            "Artesanal" to R.drawable.artesanal,
            "Delgada" to R.drawable.delgada,
            "Pan" to R.drawable.masapan,
            "CheeseBites" to R.drawable.cheesebites,
            "HutCheese" to R.drawable.hutcheese
        )
    }

    fun openMapsForLocal(context: Context, latitud: Double, longitud: Double) {
        val gmmIntentUri = Uri.parse("geo:$latitud,$longitud?q=$latitud,$longitud")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps") // Opcional: Intenta abrir solo con Google Maps

        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            // Opcional: Si Google Maps no está instalado, abre cualquier otra app de mapas
            val genericMapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            if (genericMapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(genericMapIntent)
            } else {
                // Manejar el caso en que no haya ninguna app de mapas
                // Puedes mostrar un Toast o un Snackbar
            }
        }
    }
}

