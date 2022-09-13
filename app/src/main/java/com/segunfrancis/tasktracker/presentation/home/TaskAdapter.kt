package com.segunfrancis.tasktracker.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.segunfrancis.tasktracker.R
import com.segunfrancis.tasktracker.data.local.TaskEntity
import com.segunfrancis.tasktracker.databinding.ItemTaskBinding

class TaskAdapter(
    private val onItemClick: (TaskEntity) -> Unit,
    private val onEditClick: (TaskEntity) -> Unit,
    private val onDeleteClick: (TaskEntity) -> Unit
) : ListAdapter<TaskEntity, TaskAdapter.TaskViewHolder>(TASK_DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            ItemTaskBinding.bind(
                LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
            )
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: TaskEntity) = with(binding) {
            taskText.text = task.title
            editButton.setOnClickListener { onEditClick.invoke(task) }
            deleteButton.setOnClickListener { onDeleteClick.invoke(task) }
            root.setOnClickListener { onItemClick.invoke(task) }
            val backgroundColors = listOf(
                R.color.background_green,
                R.color.dull_yellow,
                R.color.rich_blue,
                R.color.peach,
                R.color.harvest_pink,
                R.color.light_green
            )
            val randomInt = (backgroundColors.indices).random()
            parent.setBackgroundResource(backgroundColors[randomInt])
        }
    }

    companion object {
        val TASK_DIFF_UTIL = object : DiffUtil.ItemCallback<TaskEntity>() {
            override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}
