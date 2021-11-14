package com.codepolitan.kerjaanku.adapter

import android.graphics.Paint
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codepolitan.kerjaanku.R
import com.codepolitan.kerjaanku.db.DbSubTaskHelper
import com.codepolitan.kerjaanku.db.DbTaskHelper
import com.codepolitan.kerjaanku.model.SubTask
import com.codepolitan.kerjaanku.model.Task
import kotlinx.android.synthetic.main.item_task.view.*

class TaskAdapter(
    private val dbTaskHelper: DbTaskHelper,
    private val dbSubTaskHelper: DbSubTaskHelper
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(
            task: Task,
            listener: ((Task) -> Unit)?,
            dbTaskHelper: DbTaskHelper,
            dbSubTaskHelper: DbSubTaskHelper
        ) {
            itemView.tvTitleTask.text = task.mainTask?.title
            val subTaskAdapter = SubTaskAdapter(dbSubTaskHelper)

            if (task.mainTask?.isComplete!!){
                completeTask()
            }else{
                inCompleteTask()
            }

            if (task.mainTask!!.date != null && task.mainTask!!.date!!.isNotEmpty()){
                showDateTask()
                itemView.tvDateTask.text = task.mainTask!!.date
            }else{
                hideDateTask()
            }

            if (task.subTasks != null){
                showSubTasks()
                subTaskAdapter.setData(task.subTasks!!)

                itemView.rvSubTask.adapter = subTaskAdapter
            }else{
                hideSubTasks()
            }

            itemView.btnDoneTask.setOnClickListener {
                if (task.mainTask!!.isComplete){
                    task.mainTask!!.isComplete = false
                    val result = dbTaskHelper.updateTask(task.mainTask)
                    if (result > 0){
                        inCompleteTask()
                        Handler().postDelayed({
                            deleteDataTask(adapterPosition)
                        }, 500)
                        if (task.subTasks != null){
                            var isSuccess = false
                            for (subTask: SubTask in task.subTasks!!){
                                subTask.isComplete = false
                                val resultSubTask = dbSubTaskHelper.updateSubTask(subTask)
                                if (resultSubTask > 0){
                                    isSuccess = true
                                }
                            }
                            if (isSuccess){
                                subTaskAdapter.setData(task.subTasks!!)
                            }
                        }
                    }
                }else{
                    task.mainTask!!.isComplete = true
                    val result = dbTaskHelper.updateTask(task.mainTask)
                    if (result > 0){
                        completeTask()
                        Handler().postDelayed({
                            deleteDataTask(adapterPosition)
                        }, 500)
                        if (task.subTasks != null){
                            var isSuccess = false
                            for (subTask: SubTask in task.subTasks!!){
                                subTask.isComplete = true
                                val resultSubTask = dbSubTaskHelper.updateSubTask(subTask)
                                if (resultSubTask > 0){
                                    isSuccess = true
                                }
                            }
                            if (isSuccess){
                                subTaskAdapter.setData(task.subTasks!!)
                            }
                        }
                    }
                }
            }

            itemView.setOnClickListener {
                if (listener != null) {
                    listener(task)
                }
            }

            subTaskAdapter.onClick {
                if (listener != null) {
                    listener(task)
                }
            }
        }

        private fun completeTask() {
            itemView.btnDoneTask.setImageResource(R.drawable.ic_complete_task_black_24dp)
            itemView.tvTitleTask.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        private fun inCompleteTask() {
            itemView.btnDoneTask.setImageResource(R.drawable.ic_done_task_black_24dp)
            itemView.tvTitleTask.paintFlags = Paint.ANTI_ALIAS_FLAG
        }

        private fun hideSubTasks() {
            itemView.lineTask.visibility = View.GONE
            itemView.rvSubTask.visibility = View.GONE
        }

        private fun showSubTasks() {
            itemView.lineTask.visibility = View.VISIBLE
            itemView.rvSubTask.visibility = View.VISIBLE
        }

        private fun hideDateTask() {
            itemView.containerDateTask.visibility = View.GONE
        }

        private fun showDateTask() {
            itemView.containerDateTask.visibility = View.VISIBLE
        }

    }

    private var tasks = mutableListOf<Task>()
    private var listener : ((Task) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false))

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tasks[position], listener, dbTaskHelper, dbSubTaskHelper)
    }

    fun setData(tasks: List<Task>){
        this.tasks = tasks as MutableList<Task>
        notifyDataSetChanged()
    }

    fun onClick(listener: (Task) -> Unit){
        this.listener = listener
    }

    private fun deleteDataTask(position: Int){
        tasks.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, tasks.size)
    }

    fun deleteAllDataTask(){
        tasks.removeAll(tasks)
        notifyDataSetChanged()
    }
}