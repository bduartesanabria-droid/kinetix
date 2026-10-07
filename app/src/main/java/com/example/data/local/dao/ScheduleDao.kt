package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ScheduleItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Query("SELECT * FROM schedule_items ORDER BY day_of_week ASC, start_time ASC")
    fun getAllScheduleItems(): Flow<List<ScheduleItemEntity>>

    @Query("SELECT * FROM schedule_items WHERE day_of_week = :dayOfWeek ORDER BY start_time ASC")
    fun getScheduleItemsByDay(dayOfWeek: Int): Flow<List<ScheduleItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleItem(item: ScheduleItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleItems(items: List<ScheduleItemEntity>)

    @Update
    suspend fun updateScheduleItem(item: ScheduleItemEntity)

    @Query("UPDATE schedule_items SET reminder_enabled = :enabled WHERE id = :id")
    suspend fun setReminderEnabled(id: String, enabled: Boolean)

    @Query("DELETE FROM schedule_items WHERE id = :id")
    suspend fun deleteScheduleItem(id: String)
}
