package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expense_transactions",
    indices = [
        Index(value = ["category"]),
        Index(value = ["timestamp"])
    ]
)
data class ExpenseTransactionEntity(
    @PrimaryKey val id: String,
    val amount: Double,
    val concept: String,
    val category: String, // "Comida", "Juegos", "Salidas", "Transporte", "Tecnología", "Hogar", "Otros"
    val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "raw_voice_note") val rawVoiceNote: String? = null
)
