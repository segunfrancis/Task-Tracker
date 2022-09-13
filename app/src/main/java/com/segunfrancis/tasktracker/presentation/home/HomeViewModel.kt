package com.segunfrancis.tasktracker.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segunfrancis.tasktracker.R
import com.segunfrancis.tasktracker.data.local.TaskEntity
import com.segunfrancis.tasktracker.data.repository.TaskRepository
import com.segunfrancis.tasktracker.util.AppConstants.DELAY_200
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<TaskUiState>(TaskUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.update { TaskUiState.Error(throwable.localizedMessage) }
        Timber.e(throwable)
    }

    init {
        getTasks()
    }

    private fun getTasks() {
        viewModelScope.launch(exceptionHandler) {
            repository.getAllTasks()
                .catch { throwable ->
                    _uiState.update { TaskUiState.Error(throwable.localizedMessage) }
                }
                .collect { tasks ->
                    _uiState.update {
                        TaskUiState.Success(
                            tasks = tasks,
                            progresses = getRandomProgresses(),
                            colours = getRandomColours()
                        )
                    }
                }
        }
    }

    private fun getRandomProgresses(): List<Int> {
        val progresses = mutableListOf<Int>()
        repeat(6) {
            progresses.add((40 until 100).random())
        }
        return progresses
    }

    private fun getRandomColours(): List<Int> {
        val colors = listOf(
            R.color.background_green,
            R.color.dull_yellow,
            R.color.rich_blue,
            R.color.peach,
            R.color.harvest_pink,
            R.color.light_green
        )
        val coloursList = mutableListOf<Int>()
        repeat(colors.size) { coloursList.add(colors[(colors.indices).random()]) }
        return coloursList
    }

    fun addTask(task: TaskEntity) {
        viewModelScope.launch(exceptionHandler) {
            repository.addTask(task)
            delay(DELAY_200)
            _uiState.update { TaskUiState.Idle }
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch(exceptionHandler) {
            repository.updateTask(task)
            delay(DELAY_200)
            _uiState.update { TaskUiState.Idle }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch(exceptionHandler) {
            repository.deleteTask(taskId)
            _uiState.update { TaskUiState.Idle }
        }
    }

    fun onEditClick(task: TaskEntity) {
        viewModelScope.launch(exceptionHandler) {
            _uiState.update { TaskUiState.EditTask(task) }
        }
    }

    fun onItemClick(task: TaskEntity) {
        viewModelScope.launch(exceptionHandler) {
            _uiState.update { TaskUiState.ViewTask(task) }
        }
    }

    fun onBackClick() {
        viewModelScope.launch(exceptionHandler) {
            _uiState.update { TaskUiState.Idle }
        }
    }
}

sealed class TaskUiState {
    object Idle : TaskUiState()
    data class Success(
        val tasks: List<TaskEntity>,
        val progresses: List<Int>,
        val colours: List<Int>
    ) : TaskUiState()

    data class Error(val errorMessage: String?) : TaskUiState()
    data class ViewTask(val task: TaskEntity) : TaskUiState()
    data class EditTask(val task: TaskEntity) : TaskUiState()
}
