package com.codepolitan.kerjaanku.adapter

import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codepolitan.kerjaanku.R
import com.codepolitan.kerjaanku.db.DbSubTaskHelper
import com.codepolitan.kerjaanku.model.SubTask
import kotlinx.android.synthetic.main.item_add_sub_task.view.*

class AddSubTaskAdapter(private val dbSubTaskHelper: DbSubTaskHelper) : RecyclerView.Adapter<AddSubTaskAdapter.ViewHolder>() {
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(subTask: SubTask) {
            if (subTask.title != null){
                itemView.etTitleSubTask.setText(subTask.title)
            }

            if (subTask.isComplete){
                completeTask()
            }else{
                inCompleteTask()
            }

            itemView.btnRemoveSubTask.setOnClickListener {
                if (subTask.id != null){
                    val result = dbSubTaskHelper.deleteSubTask(subTask.id)
                    if (result > 0){
                        deleteTask(adapterPosition)
                    }
                }
            }

            itemView.etTitleSubTask.addTextChangedListener(object : TextWatcher{
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    subTask.title = s.toString()
                    update(subTask, adapterPosition)
                }

            })

            itemView.btnCompleteSubTask.setOnClickListener {
                if (subTask.isComplete){
                    subTask.isComplete = false
                    val result = dbSubTaskHelper.updateSubTask(subTask)
                    if (result > 0){
                        inCompleteTask()
                    }
                }else{
                    subTask.isComplete = true
                    val result = dbSubTaskHelper.updateSubTask(subTask)
                    if (result > 0){
                        completeTask()
                    }
                }
            }
        }

        private fun inCompleteTask() {
            itemView.btnCompleteSubTask.setImageResource(R.drawable.ic_done_task_black_24dp)
            itemView.etTitleSubTask.paintFlags = Paint.ANTI_ALIAS_FLAG
        }

        private fun completeTask() {
            itemView.btnCompleteSubTask.setImageResource(R.drawable.ic_complete_task_black_24dp)
            itemView.etTitleSubTask.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

    }

    private var listAddSubTask = mutableListOf<SubTask>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_add_sub_task, parent, false))

    override fun getItemCount(): Int = listAddSubTask.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listAddSubTask[position])
    }

    fun addTask(subTask: SubTask){
        listAddSubTask.add(subTask)
        notifyItemInserted(listAddSubTask.size - 1)
    }

    fun deleteTask(position: Int){
        listAddSubTask.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listAddSubTask.size)
    }

    fun update(subTask: SubTask, position: Int){
        listAddSubTask[position] = subTask
    }

    fun getData(): List<SubTask>?{
        return if (listAddSubTask.size > 0){
            listAddSubTask
        }else{
            null
        }
    }

    fun setData(subTasks: List<SubTask>){
        listAddSubTask = subTasks as MutableList<SubTask>
    }
}