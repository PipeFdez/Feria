package com.example.aplicacion_feria.data.local.dao

import androidx.room.*
import com.example.aplicacion_feria.data.local.entities.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun getAllProductos(): Flow<List<ProductoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: ProductoEntity): Long

    @Delete
    suspend fun deleteProducto(producto: ProductoEntity)
}