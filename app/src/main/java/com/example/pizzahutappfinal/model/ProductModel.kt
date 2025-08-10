package com.example.pizzahutappfinal.model

data class ProductModel(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val image: String = "",
    val categoria: String = "",
    val precio: String = "",
    val variaciones: VariacionesModel? = null,
    val adicionales: Map<String, String> = emptyMap()
)

data class VariacionesModel(
    val familiar: TamanoModel? = null,
    val grande: TamanoModel? = null,
    val mediana: TamanoModel? = null
)

data class TamanoModel(
    val Artesanal: String = "",
    val CheeseBites: String = "",
    val Delgada: String = "",
    val HutCheese: String = "",
    val Pan: String = ""
)

fun VariacionesModel.getTamano(size: String): TamanoModel? {
    return when (size.lowercase()) {
        "mediana" -> this.mediana
        "grande" -> this.grande
        "familiar" -> this.familiar
        else -> null
    }
}