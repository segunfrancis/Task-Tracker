package com.segunfrancis.tasktracker.data.repository

import com.segunfrancis.tasktracker.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun addTask(task: TaskEntity)

    fun getAllTasks(): Flow<List<TaskEntity>>

    suspend fun deleteTask(taskId: Long)
}
