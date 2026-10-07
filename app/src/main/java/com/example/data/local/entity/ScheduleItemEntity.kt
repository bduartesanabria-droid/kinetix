package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "schedule_items",
    indices = [
        Index(value = ["day_of_week"]),
        Index(value = ["type"])
    ]
)
data class ScheduleItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String, // "Estudio" or "Trabajo"
    @ColumnInfo(name = "day_of_week") val dayOfWeek: Int, // 1=Lunes, 2=Martes, ..., 7=Domingo
    @ColumnInfo(name = "day_name") val dayName: String,
    @ColumnInfo(name = "start_time") val startTime: String, // "08:00"
    @ColumnInfo(name = "end_time") val endTime: String, // "10:00"
    val location: String, // "Aula 204", "Oficina", "Remoto"
    val notes: String = "",
    @ColumnInfo(name = "color_hex") val colorHex: String = "#2563EB",
    @ColumnInfo(name = "reminder_enabled") val reminderEnabled: Boolean = true
)
