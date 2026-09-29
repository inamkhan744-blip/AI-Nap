package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LookDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM saved_looks ORDER BY timestamp DESC")
    fun getAllSavedLooks(): Flow<List<SavedLookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedLook(look: SavedLookEntity): Long

    @Query("DELETE FROM saved_looks WHERE id = :id")
    suspend fun deleteSavedLook(id: Long)

    @Update
    suspend fun updateSavedLook(look: SavedLookEntity)

    @Query("UPDATE saved_looks SET assignedDay = :day WHERE id = :id")
    suspend fun updateAssignedDay(id: Long, day: String?)

    @Query("UPDATE saved_looks SET isFavorite = :isFav WHERE id = :id")
    suspend fun toggleFavorite(id: Long, isFav: Boolean)

    @Query("UPDATE user_profile SET isPremium = :isPremium WHERE id = 1")
    suspend fun updatePremiumStatus(isPremium: Boolean)
}
