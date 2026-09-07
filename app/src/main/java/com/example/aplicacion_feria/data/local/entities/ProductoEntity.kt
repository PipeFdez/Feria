package com.example.aplicacion_feria.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TipoVenta {
    PESO,   // Kilo, 1/2, 1/4
    UNIDAD  // Unidad, atado, malla
}

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val precioBase: Int,           // CLP
    val tipoVenta: TipoVenta = TipoVenta.PESO,
    val imagenUri: String? = null
)