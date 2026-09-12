package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ResearchLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResearchDao {
    @Query("SELECT * FROM research_logs ORDER BY timestamp DESC")
    fun getAllResearchLogs(): Flow<List<ResearchLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResearchLog(log: ResearchLogEntity): Long

    @Query("DELETE FROM research_logs WHERE id = :id")
    suspend fun deleteLogById(id: Long)

    @Query("DELETE FROM research_logs")
    suspend fun clearAll()
}
