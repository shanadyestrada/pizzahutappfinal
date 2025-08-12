package com.example.pizzahutappfinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pizzahutappfinal.model.MetodoPagoModel
import com.google.firebase.firestore.FirebaseFirestore

class PaymentViewModel : ViewModel() {

    private val _metodosDePago = MutableLiveData<List<MetodoPagoModel>>()
    val metodosDePago: LiveData<List<MetodoPagoModel>> = _metodosDePago

    init {
        fetchMetodosDePago()
    }

    private fun fetchMetodosDePago() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("data")
            .document("metodoPago")
            .collection("metodo")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val metodos = querySnapshot.documents.mapNotNull { it.toObject(MetodoPagoModel::class.java) }
                _metodosDePago.value = metodos
            }
            .addOnFailureListener {
            }
    }
}