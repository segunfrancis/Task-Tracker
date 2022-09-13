package com.segunfrancis.tasktracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class TaskEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val details: String,
    val date: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val allDay: Boolean = false
)
