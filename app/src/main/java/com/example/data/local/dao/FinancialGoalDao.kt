package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.FinancialGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialGoalDao {

    @Query("SELECT * FROM financial_goals ORDER BY created_at DESC")
    fun getAllGoals(): Flow<List<FinancialGoalEntity>>

    @Query("SELECT * FROM financial_goals WHERE term = :term ORDER BY created_at DESC")
    fun getGoalsByTerm(term: String): Flow<List<FinancialGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: FinancialGoalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<FinancialGoalEntity>)

    @Update
    suspend fun updateGoal(goal: FinancialGoalEntity)

    @Query("UPDATE financial_goals SET current_amount = MIN(target_amount, current_amount + :amount), is_completed = CASE WHEN (current_amount + :amount) >= target_amount THEN 1 ELSE 0 END WHERE id = :id")
    suspend fun contributeToGoal(id: String, amount: Double)

    @Query("DELETE FROM financial_goals WHERE id = :id")
    suspend fun deleteGoal(id: String)
}
