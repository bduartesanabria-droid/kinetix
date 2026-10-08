package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "app_categories",
    indices = [
        Index(value = ["type", "name"], unique = true)
    ]
)
data class AppCategoryEntity(
    @PrimaryKey val id: String,
    val type: String, // "TASK", "SCHEDULE", "SHOPPING", "EXPENSE"
    val name: String,
    @ColumnInfo(name = "icon_name") val iconName: String = "category",
    @ColumnInfo(name = "color_hex") val colorHex: String = "#2563EB",
    @ColumnInfo(name = "is_default") val isDefault: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
