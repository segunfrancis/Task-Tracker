package com.segunfrancis.tasktracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TaskEntity::class], version = 1, exportSchema = true)
abstract class TaskTrackerDatabase : RoomDatabase() {

    abstract fun dao(): TaskTrackerDao

    companion object {
        fun getDatabase(context: Context): TaskTrackerDatabase {
            return Room.databaseBuilder(
                context,
                TaskTrackerDatabase::class.java,
                "task_tracker_database"
            ).build()
        }
    }
}
