package com.segunfrancis.tasktracker.presentation.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.segunfrancis.tasktracker.R
import com.segunfrancis.tasktracker.data.local.TaskEntity
import com.segunfrancis.tasktracker.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()
    private val taskAdapter: TaskAdapter by lazy {
        TaskAdapter(
            onItemClick = ::onItemClick,
            onEditClick = ::onEditClick,
            onDeleteClick = ::onDeleteClick
        )
    }
    private val toolbar: MaterialToolbar? by lazy {
        activity?.findViewById(R.id.toolbar)
    }
    private var currentTask: TaskEntity? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)
        binding.tasksList.adapter = taskAdapter
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest(::handleUiStates)
        }
    }

    private fun setupClickListeners() = with(binding) {
        saveButton.setOnClickListener {
            if (titleTextField.text.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (detailsTextField.text.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Details cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val task = TaskEntity(
                id = System.currentTimeMillis(),
                title = titleTextField.text.toString().trim(),
                details = detailsTextField.text.toString().trim()
            )
            if (viewModel.uiState.value is TaskUiState.EditTask) {
                currentTask?.let { cTask -> viewModel.updateTask(task.copy(id = cTask.id)) }
            } else {
                viewModel.addTask(task)
            }
            currentTask = null
        }

        toolbar?.setNavigationOnClickListener { viewModel.onBackClick() }

        editButton.setOnClickListener {
            currentTask?.let { cTask ->
                viewModel.onEditClick(cTask)
            }
            currentTask = null
        }
    }

    private fun handleUiStates(state: TaskUiState) {
        when (state) {
            is TaskUiState.Error -> Toast.makeText(
                requireContext(),
                state.errorMessage,
                Toast.LENGTH_SHORT
            ).show()
            TaskUiState.Idle -> renderIdleState()
            is TaskUiState.Success -> renderSuccess(
                tasks = state.tasks,
                progresses = state.progresses,
                colours = state.colours
            )
            is TaskUiState.EditTask -> renderEditTaskState(state.task)
            is TaskUiState.ViewTask -> renderViewTaskState(state.task)
        }
    }

    private fun renderSuccess(tasks: List<TaskEntity>, progresses: List<Int>, colours: List<Int>) =
        with(binding) {
            taskAdapter.submitList(tasks)
            toolbar?.navigationIcon = null

            val progressViews = mutableListOf(
                progressView1,
                progressView2,
                progressView3,
                progressView4,
                progressView5,
                progressView6
            )
            progressViews.forEachIndexed { index, progressView ->
                progressView.progress = progresses[index].toFloat()
                progressView.highlightView.color = ContextCompat.getColor(requireContext(), colours[index])
            }
            progressGroup.isGone = tasks.isEmpty()
        }

    private fun renderEditTaskState(task: TaskEntity) = with(binding) {
        titleTextField.setText(task.title)
        detailsTextField.setText(task.details)
        setTextFieldStates(isEditable = true)
        toolbar?.setNavigationIcon(R.drawable.ic_back)
        currentTask = task
    }

    private fun renderViewTaskState(task: TaskEntity) = with(binding) {
        titleTextField.setText(task.title)
        detailsTextField.setText(task.details)
        setTextFieldStates(isEditable = false)
        toolbar?.setNavigationIcon(R.drawable.ic_back)
        currentTask = task
    }

    private fun renderIdleState() = with(binding) {
        titleTextField.text.clear()
        detailsTextField.text.clear()
        setTextFieldStates(isEditable = true)
        toolbar?.setNavigationIcon(null)
    }

    private fun setTextFieldStates(isEditable: Boolean) = with(binding) {
        titleTextField.apply {
            isClickable = isEditable
            isFocusable = isEditable
            isFocusableInTouchMode = isEditable
            isEnabled = isEditable
        }
        detailsTextField.apply {
            isClickable = isEditable
            isFocusable = isEditable
            isFocusableInTouchMode = isEditable
            isEnabled = isEditable
        }
    }

    private fun onEditClick(task: TaskEntity) {
        viewModel.onEditClick(task)
    }

    private fun onItemClick(task: TaskEntity) {
        viewModel.onItemClick(task)
    }

    private fun onDeleteClick(task: TaskEntity) {
        viewModel.deleteTask(task.id)
        currentTask = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
