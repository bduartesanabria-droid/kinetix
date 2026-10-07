package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "financial_goals")
data class FinancialGoalEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String = "",
    val term: String, // "CORTO", "MEDIANO", "LARGO"
    @ColumnInfo(name = "current_amount") val currentAmount: Double = 0.0,
    @ColumnInfo(name = "target_amount") val targetAmount: Double,
    @ColumnInfo(name = "icon_name") val iconName: String = "flag",
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
