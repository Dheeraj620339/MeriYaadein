package com.example.meriyaadein.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Diary entry entity for Room database
 */
@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val date: Long, // Unix timestamp
    val mood: Mood,
    val isFavorite: Boolean = false,
    val isLocked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
