package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("UPDATE users SET name = :name, avatar_url = :avatarUrl, status_tag = :statusTag WHERE id = :id")
    suspend fun updateProfile(id: String, name: String, avatarUrl: String, statusTag: String)

    @Query("UPDATE users SET name = :name, avatar_url = :avatarUrl, status_tag = :statusTag, bio = :bio, custom_avatar_uri = :customAvatarUri WHERE id = :id")
    suspend fun updateFullProfile(id: String, name: String, avatarUrl: String, statusTag: String, bio: String, customAvatarUri: String?)
}
