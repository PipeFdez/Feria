package com.example.aplicacion_feria.data.local.dao

import androidx.room.*
import com.example.aplicacion_feria.data.local.entities.DetalleCompraEntity
import com.example.aplicacion_feria.data.local.entities.ProductoEntity
import com.example.aplicacion_feria.data.local.entities.SesionCompraEntity
import kotlinx.coroutines.flow.Flow

data class DetalleConProducto(
    @Embedded val detalle: DetalleCompraEntity,
    @Relation(
        parentColumn = "productoId",
        entityColumn = "id"
    )
    val producto: ProductoEntity
)

data class SesionConDetalles(
    @Embedded val sesion: SesionCompraEntity,
    @Relation(
        entity = DetalleCompraEntity::class,
        parentColumn = "id",
        entityColumn = "sesionId"
    )
    val detalles: List<DetalleConProducto>
)

@Dao
interface CompraDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSesion(sesion: SesionCompraEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetalles(detalles: List<DetalleCompraEntity>)

    @Transaction
    @Query("SELECT * FROM sesiones_compra ORDER BY fechaEpochMs DESC")
    fun getHistorialCompras(): Flow<List<SesionConDetalles>>
}