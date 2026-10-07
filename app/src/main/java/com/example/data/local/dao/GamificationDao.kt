package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.GamificationStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GamificationDao {

    @Query("SELECT * FROM gamification_stats LIMIT 1")
    fun getPrimaryStats(): Flow<GamificationStatsEntity?>

    @Query("SELECT * FROM gamification_stats WHERE user_id = :userId LIMIT 1")
    fun getStatsByUserId(userId: String): Flow<GamificationStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stats: GamificationStatsEntity)

    @Update
    suspend fun updateStats(stats: GamificationStatsEntity)

    @Query("UPDATE gamification_stats SET current_xp = current_xp + :xpDelta, today_xp_gained = today_xp_gained + :xpDelta, completed_tasks_count = completed_tasks_count + 1 WHERE user_id = :userId")
    suspend fun addXp(userId: String, xpDelta: Int)

    @Query("UPDATE gamification_stats SET current_streak = current_streak + 1 WHERE user_id = :userId")
    suspend fun incrementStreak(userId: String)
}
