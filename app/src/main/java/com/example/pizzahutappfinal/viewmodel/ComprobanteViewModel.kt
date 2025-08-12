package com.example.pizzahutappfinal.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pizzahutappfinal.model.TipoComprobanteModel
import com.google.firebase.firestore.FirebaseFirestore

class ComprobanteViewModel : ViewModel() {

    private val _tiposComprobante = MutableLiveData<List<TipoComprobanteModel>>()
    val tiposComprobante: LiveData<List<TipoComprobanteModel>> = _tiposComprobante

    init {
        fetchTiposComprobante()
    }

    private fun fetchTiposComprobante() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("data")
            .document("tipoComprobante")
            .collection("comprobante")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val comprobantes = querySnapshot.documents.mapNotNull { it.toObject(TipoComprobanteModel::class.java) }
                _tiposComprobante.value = comprobantes
            }
            .addOnFailureListener {
            }
    }
}