package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "upgrade_modules")
data class UpgradeModuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val moduleType: String, // "MODEL_ADAPTER", "AGENT_SKILL", "KERNEL_EXTENSION", "WORKFLOW_AUTOMATION"
    val targetNeed: String,
    val codeOrConfigPayload: String, // Encrypted payload of dynamically compiled instructions/logic
    val version: String,
    val ramImpactMb: Int,
    val performanceGainPercent: Int,
    val isEnabled: Boolean = true,
    val isAutoSynthesized: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
