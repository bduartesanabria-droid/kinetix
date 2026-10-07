package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    indices = [
        Index(value = ["deadline"]),
        Index(value = ["is_completed", "deadline"])
    ]
)
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // "Estudio", "Trabajo", "Vida Cotidiana"
    val urgency: String, // "ALTA", "MEDIA", "BAJA"
    val deadline: Long, // Epoch timestamp for ASC ordering
    @ColumnInfo(name = "deadline_label") val deadlineLabel: String,
    @ColumnInfo(name = "duration_label") val durationLabel: String,
    @ColumnInfo(name = "project_tag") val projectTag: String,
    @ColumnInfo(name = "context_icon") val contextIcon: String,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false,
    @ColumnInfo(name = "completed_at") val completedAt: Long? = null,
    @ColumnInfo(name = "time_slot") val timeSlot: String? = null,
    @ColumnInfo(name = "progress_percent") val progressPercent: Int = 0,
    val difficulty: String = "MEDIA" // "BAJA", "MEDIA", "ALTA"
)
