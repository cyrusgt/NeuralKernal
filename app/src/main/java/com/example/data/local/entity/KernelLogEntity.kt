package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kernel_logs")
data class KernelLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val actionType: String, // "RAM_PURGE", "POWER_PROFILE", "APP_LAUNCH", "NPU_THROTTLE", "SPEECH_STT", "PRIVACY_AUDIT"
    val description: String,
    val ramDeltaMb: Int = 0,
    val batteryImpactEstimateMilliWatts: Double = 0.0,
    val status: String = "SUCCESS",
    val timestamp: Long = System.currentTimeMillis()
)
