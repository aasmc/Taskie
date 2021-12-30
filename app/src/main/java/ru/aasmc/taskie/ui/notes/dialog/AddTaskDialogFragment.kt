package ru.aasmc.taskie.ui.notes.dialog

import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.aasmc.taskie.App
import ru.aasmc.taskie.R
import ru.aasmc.taskie.databinding.FragmentDialogNewTaskBinding
import ru.aasmc.taskie.model.PriorityColor
import ru.aasmc.taskie.model.Success
import ru.aasmc.taskie.model.Task
import ru.aasmc.taskie.model.request.AddTaskRequest
import ru.aasmc.taskie.networking.NetworkStatusChecker
import ru.aasmc.taskie.utils.toast

/**
 * Dialog fragment to create a new task.
 */
class AddTaskDialogFragment : DialogFragment() {

    private val networkStatusChecker by lazy {
        NetworkStatusChecker(activity?.getSystemService(ConnectivityManager::class.java))
    }

    private var _binding: FragmentDialogNewTaskBinding? = null
    private val binding: FragmentDialogNewTaskBinding get() = _binding!!

    private var taskAddedListener: TaskAddedListener? = null
    private val remoteApi = App.remoteApi

    interface TaskAddedListener {
        fun onTaskAdded(task: Task)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FragmentDialogTheme)
    }

    fun setTaskAddedListener(listener: TaskAddedListener) {
        taskAddedListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogNewTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initListeners()
    }

    private fun initUi() {
        context?.let {
            binding.prioritySelector.adapter =
                ArrayAdapter<PriorityColor>(
                    it,
                    android.R.layout.simple_spinner_dropdown_item,
                    PriorityColor.values()
                )
            binding.prioritySelector.setSelection(0)
        }
    }

    private fun initListeners() = binding.saveTaskAction.setOnClickListener { saveTask() }

    private fun saveTask() {
        if (isInputEmpty()) {
            context?.toast(getString(R.string.empty_fields))
            return
        }

        val title = binding.newTaskTitleInput.text.toString()
        val content = binding.newTaskDescriptionInput.text.toString()
        val priority = binding.prioritySelector.selectedItemPosition + 1
        networkStatusChecker.performIfConnectedToInternet {
            GlobalScope.launch {
                val result = remoteApi.addTask(AddTaskRequest(title, content, priority))
                withContext(Dispatchers.Main) {
                    if (result is Success) {
                        onTaskAdded(result.data)
                    } else {
                        onTaskAddFailed()
                    }
                    clearUi()
                }
            }
        }
    }


    private fun clearUi() {
        binding.newTaskTitleInput.text.clear()
        binding.newTaskDescriptionInput.text.clear()
        binding.prioritySelector.setSelection(0)
    }

    private fun isInputEmpty(): Boolean = TextUtils.isEmpty(
        binding.newTaskTitleInput.text
    ) || TextUtils.isEmpty(binding.newTaskDescriptionInput.text)

    private fun onTaskAdded(task: Task) {
        taskAddedListener?.onTaskAdded(task)
        dismiss()
    }

    private fun onTaskAddFailed() {
        this.activity?.toast("Something went wrong!")
    }
}