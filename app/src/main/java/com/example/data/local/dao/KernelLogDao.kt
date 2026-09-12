package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.KernelLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KernelLogDao {
    @Query("SELECT * FROM kernel_logs ORDER BY timestamp DESC")
    fun getAllKernelLogs(): Flow<List<KernelLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: KernelLogEntity): Long

    @Query("DELETE FROM kernel_logs WHERE id = :id")
    suspend fun deleteLogById(id: Long)

    @Query("DELETE FROM kernel_logs")
    suspend fun clearLogs()
}
