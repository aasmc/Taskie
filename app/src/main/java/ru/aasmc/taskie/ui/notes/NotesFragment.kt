package ru.aasmc.taskie.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ru.aasmc.taskie.databinding.FragmentNotesBinding
import ru.aasmc.taskie.model.Task
import ru.aasmc.taskie.networking.RemoteApi
import ru.aasmc.taskie.ui.notes.dialog.AddTaskDialogFragment
import ru.aasmc.taskie.ui.notes.dialog.TaskOptionsDialogFragment
import ru.aasmc.taskie.utils.gone
import ru.aasmc.taskie.utils.toast
import ru.aasmc.taskie.utils.visible

/**
 * Fetches and displays notes from the API.
 */
class NotesFragment : Fragment(), AddTaskDialogFragment.TaskAddedListener,
    TaskOptionsDialogFragment.TaskOptionSelectedListener {

    private var _binding: FragmentNotesBinding? = null
    private val binding: FragmentNotesBinding get() = _binding!!
    private val adapter by lazy { TaskAdapter(::onItemSelected) }
    private val remoteApi = RemoteApi()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initListeners()
    }

    private fun initUi() {
        binding.progress.visible()
        binding.noData.visible()
        binding.tasksRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.tasksRecyclerView.adapter = adapter
        getAllTasks()
    }

    private fun initListeners() {
        binding.addTask.setOnClickListener { addTask() }
    }

    private fun onItemSelected(taskId: String) {
        val dialog = TaskOptionsDialogFragment.newInstance(taskId)
        dialog.setTaskOptionSelectedListener(this)
        dialog.show(childFragmentManager, dialog.tag)
    }

    override fun onTaskAdded(task: Task) {
        adapter.addData(task)
    }

    private fun addTask() {
        val dialog = AddTaskDialogFragment()
        dialog.setTaskAddedListener(this)
        dialog.show(childFragmentManager, dialog.tag)
    }

    private fun getAllTasks() {
        binding.progress.visible()
        remoteApi.getTasks { tasks, error ->
            if (tasks.isNotEmpty()) {
                onTaskListReceived(tasks)
            } else if (error != null) {
                onGetTasksFailed()
            }
        }
    }

    private fun checkList(notes: List<Task>) {
        if (notes.isEmpty()) binding.noData.visible() else binding.noData.gone()
    }

    private fun onTasksReceived(tasks: List<Task>) = adapter.setData(tasks)

    private fun onTaskListReceived(tasks: List<Task>) {
        binding.progress.gone()
        checkList(tasks)
        onTasksReceived(tasks)
    }

    private fun onGetTasksFailed() {
        binding.progress.gone()
        activity?.toast("Failed to fetch tasks!")
    }

    override fun onTaskDeleted(taskId: String) {
        adapter.removeTask(taskId)
        activity?.toast("Task deleted!")
    }

    override fun onTaskCompleted(taskId: String) {
        adapter.removeTask(taskId)
        activity?.toast("Task completed!")
    }
}