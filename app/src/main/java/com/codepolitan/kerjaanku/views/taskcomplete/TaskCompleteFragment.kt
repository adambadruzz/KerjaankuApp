package com.codepolitan.kerjaanku.views.taskcomplete

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codepolitan.kerjaanku.R
import com.codepolitan.kerjaanku.adapter.TaskAdapter
import com.codepolitan.kerjaanku.db.DbSubTaskHelper
import com.codepolitan.kerjaanku.db.DbTaskHelper
import com.codepolitan.kerjaanku.model.Task
import com.codepolitan.kerjaanku.repository.TaskRepository
import kotlinx.android.synthetic.main.fragment_task_complete.*

class TaskCompleteFragment : Fragment() {

    private lateinit var dbTaskHelper: DbTaskHelper
    private lateinit var dbSubTaskHelper: DbSubTaskHelper
    private lateinit var taskAdapter: TaskAdapter
    private var tasks: List<Task>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task_complete, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setup()
        onClick()
    }

    private fun onClick() {
        fabDeleteTaskComplete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete All Data")
                .setMessage("Apakah anda yakin menghapus semua data?")
                .setPositiveButton("Yes"){ dialog, _ ->
                    if (tasks != null){
                        val result = dbTaskHelper.deleteAllTaskComplete()
                        if (result > 0){
                            val dialogDeleteSuccess = showSuccessDeleteAllTaskDialog()
                            Handler().postDelayed({
                                dialog.dismiss()
                                dialogDeleteSuccess.dismiss()
                                taskAdapter.deleteAllDataTask()
                            }, 1200)
                        }
                    }
                }
                .setNegativeButton("No"){ dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun showSuccessDeleteAllTaskDialog(): AlertDialog {
        return AlertDialog.Builder(context)
            .setTitle("Success")
            .setMessage("Berhasil menghapus semua data")
            .show()
    }

    private fun setup() {
        dbTaskHelper = DbTaskHelper.getInstance(context)
        dbSubTaskHelper = DbSubTaskHelper.getInstance(context)
        taskAdapter = TaskAdapter(dbTaskHelper, dbSubTaskHelper)
    }

    override fun onResume() {
        super.onResume()
        getDataTask()
    }

    private fun getDataTask() {
        tasks = TaskRepository.getDataTaskCompleteFromDatabase(dbTaskHelper, dbSubTaskHelper)

        if (tasks != null && tasks!!.isNotEmpty()){
            showTaskComplete()
            taskAdapter.setData(tasks!!)

            rvTaskComplete.adapter = taskAdapter
        }else{
            hideTaskComplete()
        }
    }

    private fun hideTaskComplete() {
        rvTaskComplete.visibility = View.GONE
        layoutEmptyTaskComplete.visibility = View.VISIBLE
        fabDeleteTaskComplete.visibility = View.GONE
    }

    private fun showTaskComplete() {
        rvTaskComplete.visibility = View.VISIBLE
        layoutEmptyTaskComplete.visibility = View.GONE
        fabDeleteTaskComplete.visibility = View.VISIBLE
    }
}
