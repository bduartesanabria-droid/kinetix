package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ExpenseTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expense_transactions ORDER BY timestamp DESC")
    fun getAllExpenses(): Flow<List<ExpenseTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseTransactionEntity)

    @Query("DELETE FROM expense_transactions WHERE id = :id")
    suspend fun deleteExpense(id: String)

    @Query("DELETE FROM expense_transactions")
    suspend fun clearAllExpenses()
}
