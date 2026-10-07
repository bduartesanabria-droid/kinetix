package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.FinancialProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialDao {

    @Query("SELECT * FROM financial_profile LIMIT 1")
    fun getFinancialProfile(): Flow<FinancialProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: FinancialProfileEntity)

    @Update
    suspend fun update(profile: FinancialProfileEntity)

    @Query("UPDATE financial_profile SET monthly_income = :income, rent_housing = :rent, other_fixed_expenses = :other WHERE id = :id")
    suspend fun updateBaseFinancials(id: String, income: Double, rent: Double, other: Double)
}
