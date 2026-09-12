package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallpapers")
data class WallpaperEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val prompt: String,
    val imageUrl: String, // Resource name or URL
    val category: String, // "AI Generated", "Cyberpunk", "Nature", "Abstract", "Cosmic"
    val author: String,
    val isDownloaded: Boolean = true,
    val isCurrentWallpaper: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
