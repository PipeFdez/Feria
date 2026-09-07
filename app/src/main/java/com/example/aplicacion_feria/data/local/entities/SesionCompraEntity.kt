package com.example.aplicacion_feria.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sesiones_compra")
data class SesionCompraEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fechaEpochMs: Long = System.currentTimeMillis(),
    val totalGastado: Int,
    val montoPagado: Int,
    val vuelto: Int,
    val presupuestoLimite: Int? = null
)