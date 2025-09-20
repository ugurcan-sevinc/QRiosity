package com.ugrcaan.qriosity.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedLinkDao {
    @Query("SELECT * FROM saved_links ORDER BY createdAt DESC")
    fun observeSavedLinks(): Flow<List<SavedLinkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(entity: SavedLinkEntity)

    @Query("DELETE FROM saved_links WHERE id = :id")
    suspend fun deleteLink(id: Long)
}
