package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_agents")
data class AgentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val role: String,
    val description: String,
    val systemPrompt: String,
    val toolsJson: String, // e.g. ["LOCAL_RAG", "WEB_SEARCH", "DEVICE_CONTROL", "APP_LAUNCH", "VOICE_SYNTH"]
    val executionMode: String = "AUTONOMOUS", // "AUTONOMOUS", "MANUAL", "CRON_INTERVAL"
    val isEnabled: Boolean = true,
    val runCount: Int = 0,
    val lastRunTimestamp: Long = 0,
    val lastOutput: String = "",
    val iconName: String = "bolt"
)
