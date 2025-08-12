package com.example.pizzahutappfinal.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pizzahutappfinal.model.LocalModel
import com.google.firebase.firestore.FirebaseFirestore

class LocalViewModel : ViewModel() {
    private val _locales = MutableLiveData<List<LocalModel>>()
    val locales: LiveData<List<LocalModel>> = _locales

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadLocales()
    }

    private fun loadLocales() {
        _isLoading.value = true
        FirebaseFirestore.getInstance().collection("data").document("local").collection("locales")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val localesList = querySnapshot.toObjects(LocalModel::class.java)
                _locales.value = localesList
                _isLoading.value = false
            }
            .addOnFailureListener {
                _locales.value = emptyList()
                _isLoading.value = false
            }
    }
}