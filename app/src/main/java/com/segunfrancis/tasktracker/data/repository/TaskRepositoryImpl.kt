package com.segunfrancis.tasktracker.data.repository

import com.segunfrancis.tasktracker.data.local.TaskEntity
import com.segunfrancis.tasktracker.data.local.TaskTrackerDao
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class TaskRepositoryImpl @Inject constructor(
    private val dao: TaskTrackerDao,
    private val dispatcher: CoroutineDispatcher
) : TaskRepository {
    override suspend fun addTask(task: TaskEntity) {
        withContext(dispatcher) { dao.addTask(task) }
    }

    override suspend fun updateTask(task: TaskEntity) {
        withContext(dispatcher) { dao.updateTask(task) }
    }

    override fun getAllTasks(): Flow<List<TaskEntity>> {
        return dao.getAllTasks().flowOn(dispatcher)
    }

    override suspend fun deleteTask(taskId: Long) {
        withContext(dispatcher) { dao.deleteTask(taskId) }
    }
}
