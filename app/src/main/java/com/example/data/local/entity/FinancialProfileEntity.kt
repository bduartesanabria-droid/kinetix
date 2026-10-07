package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "financial_profile")
data class FinancialProfileEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "month_label") val monthLabel: String = "Octubre 2024",
    @ColumnInfo(name = "monthly_income") val monthlyIncome: Double = 4500000.0,
    @ColumnInfo(name = "rent_housing") val rentHousing: Double = 1200000.0,
    @ColumnInfo(name = "other_fixed_expenses") val otherFixedExpenses: Double = 800000.0,
    @ColumnInfo(name = "emergency_fund_current") val emergencyFundCurrent: Double = 3500000.0,
    @ColumnInfo(name = "emergency_fund_goal") val emergencyFundGoal: Double = 5000000.0,
    @ColumnInfo(name = "trip_fund_current") val tripFundCurrent: Double = 1200000.0,
    @ColumnInfo(name = "trip_fund_goal") val tripFundGoal: Double = 2000000.0,
    @ColumnInfo(name = "status_tag") val statusTag: String = "Saludable"
)
