package com.example.aplicacion_feria.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion_feria.data.local.FeriaDatabase
import com.example.aplicacion_feria.data.local.dao.SesionConDetalles
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HistorialViewModel(application: Application) : AndroidViewModel(application) {
    private val db = FeriaDatabase.getDatabase(application)
    private val compraDao = db.compraDao()

    // Conectado directamente al Flow del DAO: si guardas una compra, se actualiza sola la lista
    val historial: StateFlow<List<SesionConDetalles>> = compraDao.getHistorialCompras()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}