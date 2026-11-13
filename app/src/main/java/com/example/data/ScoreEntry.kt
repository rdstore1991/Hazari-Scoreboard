package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "score_entries")
data class ScoreEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playerId: Int,
    val points: Int,
    val timestamp: Long = System.currentTimeMillis()
)
