package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String,
    @ColumnInfo(name = "level_title") val levelTitle: String,
    @ColumnInfo(name = "level_number") val levelNumber: Int,
    @ColumnInfo(name = "status_tag") val statusTag: String
)
