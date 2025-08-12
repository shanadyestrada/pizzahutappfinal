package com.example.pizzahutappfinal.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pizzahutappfinal.model.LocalModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalPage(modifier: Modifier = Modifier) {
    var locales by remember { mutableStateOf<List<LocalModel>>(emptyList()) }
    var selectedLocal by remember { mutableStateOf<LocalModel?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val defaultCameraPosition = LatLng(-11.990550902345337, -77.06284148059153)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultCameraPosition, 10f)
    }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("data/local/locales")
            .get()
            .addOnSuccessListener { result ->
                val lista = mutableListOf<LocalModel>()
                for (document in result) {
                    val local = document.toObject(LocalModel::class.java)
                    lista.add(local)
                }
                locales = lista
                Log.d("FirestoreData", "Numero de locales obtenidos: ${locales.size}")
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreData", "Error al obtener los locales", exception)
            }
    }

    LaunchedEffect(selectedLocal) {
        selectedLocal?.let {
            val newPosition = LatLng(it.latitud, it.longitud)
            launch {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(newPosition, 15f),
                    durationMs = 1000
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dropdown para seleccionar el local
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                readOnly = true,
                value = selectedLocal?.nombre ?: "Selecciona un local",
                onValueChange = {},
                label = { Text("Local") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                locales.forEach { local ->
                    DropdownMenuItem(
                        text = { Text(local.nombre) },
                        onClick = {
                            selectedLocal = local
                            expanded = false
                        }
                    )
                }
            }
        }

        selectedLocal?.let { local ->
            Text(
                text = "Dirección: ${local.direccion}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Horario de Atención: 11:30AM a 11:00PM",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Usa weight para que ocupe el resto del espacio
                .padding(top = 8.dp),
            cameraPositionState = cameraPositionState
        ) {
            // Muestra todos los marcadores en el mapa
            locales.forEach { local ->
                Marker(
                    state = MarkerState(position = LatLng(local.latitud, local.longitud)),
                    title = local.nombre,
                    snippet = local.direccion
                )
            }
        }

    }

}