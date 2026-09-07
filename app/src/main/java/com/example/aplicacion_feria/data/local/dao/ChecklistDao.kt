package com.example.aplicacion_feria.data.local.dao

import androidx.room.*
import com.example.aplicacion_feria.data.local.entities.ItemChecklistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklist_items ORDER BY id ASC")
    fun getChecklist(): Flow<List<ItemChecklistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemChecklistEntity)

    @Update
    suspend fun updateItem(item: ItemChecklistEntity)

    @Query("DELETE FROM checklist_items")
    suspend fun clearChecklist()
}