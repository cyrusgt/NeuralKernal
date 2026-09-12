package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AgentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentDao {
    @Query("SELECT * FROM ai_agents ORDER BY id ASC")
    fun getAllAgents(): Flow<List<AgentEntity>>

    @Query("SELECT * FROM ai_agents WHERE id = :id")
    suspend fun getAgentById(id: Long): AgentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgent(agent: AgentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(agents: List<AgentEntity>)

    @Update
    suspend fun updateAgent(agent: AgentEntity)

    @Query("DELETE FROM ai_agents WHERE id = :id")
    suspend fun deleteAgentById(id: Long)

    @Query("SELECT COUNT(*) FROM ai_agents")
    suspend fun getAgentCount(): Int
}
