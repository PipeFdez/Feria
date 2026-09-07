package com.example.aplicacion_feria.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "detalles_compra",
    foreignKeys = [
        ForeignKey(
            entity = SesionCompraEntity::class,
            parentColumns = ["id"],
            childColumns = ["sesionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductoEntity::class,
            parentColumns = ["id"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("sesionId"), Index("productoId")]
)
data class DetalleCompraEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sesionId: Long,
    val productoId: Long,
    val precioUnitarioCobrado: Int,
    val cantidad: Double,
    val subtotal: Int
)