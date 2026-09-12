package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "research_logs")
data class ResearchLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val query: String,
    val summary: String,
    val sourceUrlsJson: String,
    val keyTakeawaysJson: String,
    val isEncrypted: Boolean = true,
    val tokenUsage: Int = 0,
    val retrievalLatencyMs: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
)
