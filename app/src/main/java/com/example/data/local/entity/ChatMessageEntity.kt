package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String, // "user", "assistant", "system", "agent"
    val content: String,
    val isEncrypted: Boolean = true,
    val modelProfile: String = "Vivo-Quant-1.8B",
    val tokensGenerated: Int = 0,
    val latencyMs: Long = 0,
    val tokensPerSec: Double = 0.0,
    val isWebRetrieved: Boolean = false,
    val sources: String = "", // JSON list of URLs/sources
    val documentCitations: String = "", // Document title/chunk references
    val kernelActionsExecuted: String = "", // e.g. "PURGE_RAM, BATTERY_SAVER_ON"
    val timestamp: Long = System.currentTimeMillis()
)
