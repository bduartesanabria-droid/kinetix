package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TaskDao {

    // SQL query strictly matching prompt requirement:
    // "SELECT * FROM tasks WHERE is_completed = 0 ORDER BY deadline ASC"
    @Query("SELECT * FROM tasks WHERE is_completed = 0 ORDER BY deadline ASC")
    abstract fun getPendingTasksOrderedByDeadline(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks ORDER BY is_completed ASC, deadline ASC")
    abstract fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY is_completed ASC, deadline ASC")
    abstract fun getTasksByCategory(category: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE is_completed = 0 AND category = :category ORDER BY deadline ASC")
    abstract fun getPendingTasksByCategory(category: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    abstract suspend fun updateTask(task: TaskEntity)

    @Query("UPDATE tasks SET is_completed = :isCompleted, completed_at = :completedAt WHERE id = :taskId")
    abstract suspend fun setTaskCompletion(taskId: String, isCompleted: Boolean, completedAt: Long?)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    abstract suspend fun deleteTaskById(taskId: String)

    @Query("UPDATE gamification_stats SET current_xp = current_xp + :xpDelta, today_xp_gained = today_xp_gained + :xpDelta, completed_tasks_count = completed_tasks_count + 1 WHERE user_id = :userId")
    abstract suspend fun incrementGamification(userId: String, xpDelta: Int)

    // Requirement 4: Transacción atómica (BEGIN TRANSACTION) al completar tarea
    @Transaction
    open suspend fun completeTaskAtomic(taskId: String, userId: String, isCompleted: Boolean, now: Long, xpDelta: Int = 40) {
        setTaskCompletion(taskId, isCompleted, if (isCompleted) now else null)
        if (isCompleted) {
            incrementGamification(userId, xpDelta)
        }
    }
}
