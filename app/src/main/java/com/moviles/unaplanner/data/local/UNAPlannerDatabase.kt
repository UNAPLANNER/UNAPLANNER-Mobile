package com.moviles.unaplanner.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CalendarEventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class UNAPlannerDatabase : RoomDatabase() {

    abstract fun calendarEventDao(): CalendarEventDao

    companion object {
        @Volatile
        private var INSTANCE: UNAPlannerDatabase? = null

        fun getInstance(context: Context): UNAPlannerDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    UNAPlannerDatabase::class.java,
                    "unaplanner_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
