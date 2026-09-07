package com.example.aplicacion_feria.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aplicacion_feria.data.local.dao.ChecklistDao
import com.example.aplicacion_feria.data.local.dao.CompraDao
import com.example.aplicacion_feria.data.local.dao.ProductoDao
import com.example.aplicacion_feria.data.local.entities.DetalleCompraEntity
import com.example.aplicacion_feria.data.local.entities.ItemChecklistEntity
import com.example.aplicacion_feria.data.local.entities.ProductoEntity
import com.example.aplicacion_feria.data.local.entities.SesionCompraEntity

@Database(
    entities = [
        ProductoEntity::class,
        SesionCompraEntity::class,
        DetalleCompraEntity::class,
        ItemChecklistEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FeriaDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun compraDao(): CompraDao
    abstract fun checklistDao(): ChecklistDao

    companion object {
        @Volatile
        private var INSTANCE: FeriaDatabase? = null

        fun getDatabase(context: Context): FeriaDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    FeriaDatabase::class.java,
                    "feria_database.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}