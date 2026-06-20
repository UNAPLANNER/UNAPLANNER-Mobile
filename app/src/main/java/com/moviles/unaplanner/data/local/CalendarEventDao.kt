package com.moviles.unaplanner.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarEventDao {
    @Query("SELECT * FROM calendar_events WHERE userId = :userId ORDER BY activityDate ASC")
    fun getEventsByUserIdFlow(userId: Int): Flow<List<CalendarEventEntity>>

    @Query("SELECT * FROM calendar_events WHERE userId = :userId ORDER BY activityDate ASC")
    suspend fun getEventsByUserId(userId: Int): List<CalendarEventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(events: List<CalendarEventEntity>)

    @Query("DELETE FROM calendar_events WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Int)

    @Query("SELECT COUNT(*) FROM calendar_events WHERE userId = :userId")
    suspend fun countByUserId(userId: Int): Int
}
