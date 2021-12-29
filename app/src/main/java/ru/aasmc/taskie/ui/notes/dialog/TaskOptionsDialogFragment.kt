package ru.aasmc.taskie.ui.notes.dialog

import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import ru.aasmc.taskie.App
import ru.aasmc.taskie.R
import ru.aasmc.taskie.databinding.FragmentDialogTaskOptionsBinding
import ru.aasmc.taskie.networking.NetworkStatusChecker

/**
 * Displays the options to delete or complete a task.
 */
class TaskOptionsDialogFragment : DialogFragment() {
    private val networkStatusChecker by lazy {
        NetworkStatusChecker(activity?.getSystemService(ConnectivityManager::class.java))
    }
    private var _binding: FragmentDialogTaskOptionsBinding? = null
    private val binding: FragmentDialogTaskOptionsBinding = _binding!!
    private var taskOptionSelectedListener: TaskOptionSelectedListener? = null

    private val remoteApi = App.remoteApi

    companion object {
        private const val KEY_TASK_ID = "task_id"

        fun newInstance(taskId: String): TaskOptionsDialogFragment =
            TaskOptionsDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_TASK_ID, taskId)
                }
            }
    }

    interface TaskOptionSelectedListener {
        fun onTaskDeleted(taskId: String)

        fun onTaskCompleted(taskId: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FragmentDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogTaskOptionsBinding.inflate(inflater, container, false)
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
    }

    private fun initUi() {
        val taskId = arguments?.getString(KEY_TASK_ID) ?: ""
        if (taskId.isEmpty()) dismissAllowingStateLoss()

        binding.deleteTask.setOnClickListener {
            networkStatusChecker.performIfConnectedToInternet {
                remoteApi.deleteTask { error ->
                    activity?.runOnUiThread {
                        if (error == null) {
                            taskOptionSelectedListener?.onTaskDeleted(taskId)
                        }
                        dismissAllowingStateLoss()
                    }
                }
            }
        }

        binding.completeTask.setOnClickListener {
            networkStatusChecker.performIfConnectedToInternet {
                remoteApi.completeTask(taskId) { error ->
                    activity?.runOnUiThread {
                        if (error == null) {
                            taskOptionSelectedListener?.onTaskCompleted(taskId)
                        }
                        dismissAllowingStateLoss()
                    }
                }
            }
        }
    }

    fun setTaskOptionSelectedListener(taskOptionSelectedListener: TaskOptionSelectedListener) {
        this.taskOptionSelectedListener = taskOptionSelectedListener
    }
}