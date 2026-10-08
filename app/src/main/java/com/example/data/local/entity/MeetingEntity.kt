package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meetings",
    indices = [
        Index(value = ["start_time_epoch"]),
        Index(value = ["date_key"])
    ]
)
data class MeetingEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String = "",
    @ColumnInfo(name = "date_key") val dateKey: String, // e.g. "2026-10-15"
    @ColumnInfo(name = "date_display") val dateDisplay: String, // e.g. "Jueves, 15 de Octubre"
    @ColumnInfo(name = "time_display") val timeDisplay: String, // e.g. "14:30"
    @ColumnInfo(name = "start_time_epoch") val startTimeEpoch: Long,
    @ColumnInfo(name = "is_virtual") val isVirtual: Boolean = true,
    @ColumnInfo(name = "platform_or_link") val platformOrLink: String = "", // e.g. "Google Meet: https://meet.google.com/..."
    @ColumnInfo(name = "physical_location") val physicalLocation: String = "", // e.g. "Oficina Piso 3"
    @ColumnInfo(name = "notify_one_hour_before") val notifyOneHourBefore: Boolean = true,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false,
    @ColumnInfo(name = "raw_voice_note") val rawVoiceNote: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
