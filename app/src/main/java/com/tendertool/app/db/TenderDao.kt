package com.tendertool.app.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tendertool.app.models.BaseTender
import kotlinx.coroutines.flow.Flow

@Dao
interface TenderDao {

    // Gets all tenders from the watchlist and returns them as a Flow.
    // The UI can "observe" this Flow and will auto-update when the data changes.
    @Query("SELECT * FROM BaseTender")
    fun getWatchlistTenders(): Flow<List<BaseTender>>

    // Inserts a list of tenders. If one already exists (same id), it replaces it.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tenders: List<BaseTender>)

    // Deletes everything from the table.
    @Query("DELETE FROM BaseTender")
    suspend fun clearWatchlist()
}