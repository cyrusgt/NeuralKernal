package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val fileType: String, // "PDF", "TXT", "MD", "JSON", "DOCX", "MANUAL"
    val content: String,
    val summary: String = "",
    val chunkCount: Int = 0,
    val chunksJson: String = "[]", // List of chunk texts
    val wordCount: Int = 0,
    val isEncrypted: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
