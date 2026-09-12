package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.UpgradeModuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UpgradeDao {
    @Query("SELECT * FROM upgrade_modules ORDER BY timestamp DESC")
    fun getAllUpgradeModules(): Flow<List<UpgradeModuleEntity>>

    @Query("SELECT * FROM upgrade_modules WHERE isEnabled = 1")
    fun getActiveUpgradeModules(): Flow<List<UpgradeModuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpgradeModule(module: UpgradeModuleEntity): Long

    @Update
    suspend fun updateUpgradeModule(module: UpgradeModuleEntity)

    @Query("UPDATE upgrade_modules SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun toggleUpgradeModule(id: Long, isEnabled: Boolean)

    @Query("DELETE FROM upgrade_modules WHERE id = :id")
    suspend fun deleteUpgradeModule(id: Long)

    @Query("SELECT COUNT(*) FROM upgrade_modules")
    suspend fun getModuleCount(): Int
}
