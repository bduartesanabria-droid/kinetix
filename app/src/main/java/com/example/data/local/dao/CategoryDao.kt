package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.AppCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM app_categories WHERE type = :type ORDER BY created_at ASC")
    fun getCategoriesByType(type: String): Flow<List<AppCategoryEntity>>

    @Query("SELECT * FROM app_categories ORDER BY type ASC, created_at ASC")
    fun getAllCategories(): Flow<List<AppCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: AppCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategories(categories: List<AppCategoryEntity>)

    @Query("DELETE FROM app_categories WHERE id = :id")
    suspend fun deleteCategoryById(id: String)

    @Query("DELETE FROM app_categories WHERE type = :type AND name = :name")
    suspend fun deleteCategoryByNameAndType(name: String, type: String)

    @Query("SELECT COUNT(*) FROM app_categories WHERE type = :type")
    suspend fun countCategoriesByType(type: String): Int
}
