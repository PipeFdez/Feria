package com.example.aplicacion_feria.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checklist_items")
data class ItemChecklistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val comprado: Boolean = false,
    val cantidadEstimada: String? = null
)