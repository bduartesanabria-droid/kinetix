package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gamification_stats")
data class GamificationStatsEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "current_streak") val currentStreak: Int = 14,
    @ColumnInfo(name = "record_streak") val recordStreak: Int = 14,
    @ColumnInfo(name = "current_xp") val currentXp: Int = 2450,
    @ColumnInfo(name = "target_xp") val targetXp: Int = 3000,
    @ColumnInfo(name = "today_xp_gained") val todayXpGained: Int = 120,
    val level: Int = 7,
    @ColumnInfo(name = "level_title") val levelTitle: String = "Nivel 7: Maestro de la Rutina",
    @ColumnInfo(name = "completed_tasks_count") val completedTasksCount: Int = 24,
    @ColumnInfo(name = "tasks_on_time_percent") val tasksOnTimePercent: Int = 86,
    @ColumnInfo(name = "impulse_expenses") val impulseExpenses: Double = 0.0
)
