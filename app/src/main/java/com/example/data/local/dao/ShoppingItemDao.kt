package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ShoppingItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingItemDao {

    @Query("SELECT * FROM shopping_items ORDER BY is_bought ASC, is_priority DESC")
    fun getAllShoppingItems(): Flow<List<ShoppingItemEntity>>

    @Query("SELECT * FROM shopping_items WHERE is_bought = 0 ORDER BY is_priority DESC")
    fun getPendingItems(): Flow<List<ShoppingItemEntity>>

    @Query("SELECT * FROM shopping_items WHERE is_bought = 1")
    fun getBoughtItems(): Flow<List<ShoppingItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ShoppingItemEntity>)

    @Update
    suspend fun updateItem(item: ShoppingItemEntity)

    @Query("UPDATE shopping_items SET is_bought = :isBought, bought_at = :boughtAt WHERE id = :itemId")
    suspend fun setItemBoughtWithTimestamp(itemId: String, isBought: Boolean, boughtAt: Long?)

    @Query("UPDATE shopping_items SET is_bought = :isBought WHERE id = :itemId")
    suspend fun setItemBought(itemId: String, isBought: Boolean)

    @Query("DELETE FROM shopping_items WHERE id = :itemId")
    suspend fun deleteItemById(itemId: String)

    @Query("DELETE FROM shopping_items WHERE is_bought = 1")
    suspend fun clearBoughtItems()

    @Query("DELETE FROM shopping_items WHERE is_bought = 1 AND bought_at IS NOT NULL AND bought_at < :thresholdTime")
    suspend fun deleteOldPurchasedItems(thresholdTime: Long)
}
