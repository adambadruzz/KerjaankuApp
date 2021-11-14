package com.codepolitan.kerjaanku.views.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codepolitan.kerjaanku.R
import com.codepolitan.kerjaanku.adapter.TaskAdapter
import com.codepolitan.kerjaanku.db.DbSubTaskHelper
import com.codepolitan.kerjaanku.db.DbTaskHelper
import com.codepolitan.kerjaanku.repository.TaskRepository
import com.codepolitan.kerjaanku.views.newtask.NewTaskActivity
import com.codepolitan.kerjaanku.views.newtask.NewTaskActivity.Companion.EXTRA_TASK
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.startActivity

class HomeFragment : Fragment() {

    private lateinit var dbTaskHelper: DbTaskHelper
    private lateinit var dbSubTaskHelper: DbSubTaskHelper
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setup()
        onClick()
    }

    private fun onClick() {
        taskAdapter.onClick {
            context?.startActivity<NewTaskActivity>(EXTRA_TASK to it)
        }
    }

    override fun onResume() {
        super.onResume()
        getDataTask()
    }

    private fun getDataTask() {
        val tasks = TaskRepository.getDataTaskFromDatabase(dbTaskHelper, dbSubTaskHelper)

        if (tasks != null && tasks.isNotEmpty()){
            showTasks()
            taskAdapter.setData(tasks)

            rvTask.adapter = taskAdapter
        }else{
            hideTasks()
        }
    }

    private fun setup() {
        dbTaskHelper = DbTaskHelper.getInstance(context)
        dbSubTaskHelper = DbSubTaskHelper.getInstance(context)
        taskAdapter = TaskAdapter(dbTaskHelper, dbSubTaskHelper)
    }

    private fun hideTasks() {
        rvTask.visibility = View.GONE
        layoutEmptyTask.visibility = View.VISIBLE
    }

    private fun showTasks() {
        rvTask.visibility = View.VISIBLE
        layoutEmptyTask.visibility = View.GONE
    }
}
