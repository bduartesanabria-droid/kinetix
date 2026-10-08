package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items")
data class ShoppingItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    @ColumnInfo(name = "icon_name") val iconName: String,
    @ColumnInfo(name = "is_bought") val isBought: Boolean = false,
    @ColumnInfo(name = "is_priority") val isPriority: Boolean = false,
    @ColumnInfo(name = "estimated_price") val estimatedPrice: Double = 0.0,
    @ColumnInfo(name = "bought_at") val boughtAt: Long? = null,
    @ColumnInfo(name = "store_category") val storeCategory: String = "Supermercado"
)
