package com.segunfrancis.tasktracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskTrackerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("SELECT * FROM task ORDER BY id DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("DELETE FROM task WHERE id is :taskId")
    suspend fun deleteTask(taskId: Long)
}
