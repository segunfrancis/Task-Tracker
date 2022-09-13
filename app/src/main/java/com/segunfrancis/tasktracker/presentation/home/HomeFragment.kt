package com.segunfrancis.tasktracker.presentation.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
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
            if (titleTextField.text.isNullOrBlank()) return@setOnClickListener
            if (detailsTextField.text.isNullOrBlank()) return@setOnClickListener
            val task = TaskEntity(
                id = System.currentTimeMillis(),
                title = titleTextField.text.toString().trim(),
                details = detailsTextField.text.toString().trim()
            )
            viewModel.addTask(task)
        }
        toolbar?.setNavigationOnClickListener { viewModel.onBackClick() }
    }

    private fun handleUiStates(state: TaskUiState) {
        when (state) {
            is TaskUiState.Error -> Toast.makeText(
                requireContext(),
                state.errorMessage,
                Toast.LENGTH_SHORT
            ).show()
            TaskUiState.Idle -> renderIdleState()
            is TaskUiState.Success -> setupList(state.tasks)
            is TaskUiState.EditTask -> renderEditTaskState(state.task)
            is TaskUiState.ViewTask -> renderViewTaskState(state.task)
        }
    }

    private fun setupList(tasks: List<TaskEntity>) = with(binding) {
        taskAdapter.submitList(tasks)
        toolbar?.setNavigationIcon(null)
    }

    private fun renderEditTaskState(task: TaskEntity) = with(binding) {
        titleTextField.setText(task.title)
        detailsTextField.setText(task.details)
        setTextFieldStates(isEditable = true)
        toolbar?.setNavigationIcon(R.drawable.ic_back)
    }

    private fun renderViewTaskState(task: TaskEntity) = with(binding) {
        titleTextField.setText(task.title)
        detailsTextField.setText(task.details)
        setTextFieldStates(isEditable = false)
        toolbar?.setNavigationIcon(R.drawable.ic_back)
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
