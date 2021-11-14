package com.codepolitan.kerjaanku.views.newtask

import android.app.AlertDialog
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import com.codepolitan.kerjaanku.R
import com.codepolitan.kerjaanku.adapter.AddSubTaskAdapter
import com.codepolitan.kerjaanku.db.DbSubTaskHelper
import com.codepolitan.kerjaanku.db.DbTaskHelper
import com.codepolitan.kerjaanku.model.MainTask
import com.codepolitan.kerjaanku.model.SubTask
import com.codepolitan.kerjaanku.model.Task
import com.codepolitan.kerjaanku.util.DateKerjaanKu
import kotlinx.android.synthetic.main.activity_new_task.*

class NewTaskActivity : AppCompatActivity() {

    companion object{
        const val EXTRA_TASK = "extra_task"
    }

    private lateinit var addSubTaskAdapter: AddSubTaskAdapter
    private lateinit var dbTaskHelper: DbTaskHelper
    private lateinit var dbSubTaskHelper: DbSubTaskHelper
    private var isEdit = false
    private var delayedTime: Long = 1200
    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)

        setup()
        setupActionBar()
        setupAddSubTaskAdapter()
        onClick()
    }

    private fun setup() {
        dbTaskHelper = DbTaskHelper.getInstance(this)
        dbSubTaskHelper = DbSubTaskHelper.getInstance(this)
        addSubTaskAdapter = AddSubTaskAdapter(dbSubTaskHelper)

        getDataExtra()
    }

    private fun getDataExtra() {
        if(intent != null){
            task = intent.getParcelableExtra(EXTRA_TASK)
        }
        if (task != null){
            isEdit = true
            btnSubmitTask.text = getString(R.string.update)

            setupView(task)
        }else{
            task = Task(mainTask = MainTask())
        }
    }

    private fun setupView(task: Task?) {
        etTitleTask.setText(task?.mainTask?.title)
        etAddDetailsTask.setText(task?.mainTask?.details)
        val dateString = task?.mainTask?.date

        if (dateString != null){
            btnAddDateTask.text = DateKerjaanKu.dateFromSqlToDateViewTask(dateString)
            checkIsDateFilled(true)
        }
    }

    private fun setupAddSubTaskAdapter() {
        task?.subTasks?.let { addSubTaskAdapter.setData(it) }
        rvAddSubTask.adapter = addSubTaskAdapter
    }

    private fun onClick() {
        tbNewTask.setNavigationOnClickListener {
            finish()
        }

        btnAddDateTask.setOnClickListener {
            DateKerjaanKu.showDatePicker(this,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    val dateString = DateKerjaanKu.dateFormatSql(year, month, dayOfMonth)
                    btnAddDateTask.text = DateKerjaanKu.dateFromSqlToDateViewTask(dateString)

                    task?.mainTask?.date = dateString
                    checkIsDateFilled(true)
                })
        }

        btnRemoveDateTask.setOnClickListener {
            btnAddDateTask.text = null
            checkIsDateFilled(false)
        }

        btnAddSubTask.setOnClickListener {
            val subTask = SubTask(null, null, "")
            addSubTaskAdapter.addTask(subTask)
        }

        btnSubmitTask.setOnClickListener {
            submitDataToDatabase()
        }
    }

    private fun submitDataToDatabase() {
        val titleTask = etTitleTask.text.toString()
        val detailTask = etAddDetailsTask.text.toString()
        val dataSubTasks = addSubTaskAdapter.getData()

        if (titleTask.isEmpty()){
            etTitleTask.error = getString(R.string.please_field_your_title)
            etTitleTask.requestFocus()
            return
        }

        task?.mainTask?.title = titleTask

        if (detailTask.isNotEmpty()){
            task?.mainTask?.details = detailTask
        }

        if (isEdit){
            val result = dbTaskHelper.updateTask(task?.mainTask)
            if (result > 0){
                if (dataSubTasks != null && dataSubTasks.isNotEmpty()){
                    var isSuccess = false
                    for (subTask: SubTask in dataSubTasks){
                        if (subTask.id != null){
                            val resultSubTask = dbSubTaskHelper.updateSubTask(subTask)
                            isSuccess = resultSubTask > 0
                        }else{
                            subTask.idTask = task?.mainTask?.id
                            val resultSubTask = dbSubTaskHelper.insert(subTask)
                            isSuccess = resultSubTask > 0
                        }
                    }
                    if (isSuccess){
                        val dialog = showSuccessDialog(getString(R.string.sucess_update_data_to_database))
                        Handler().postDelayed({
                            dialog.dismiss()
                        }, 1200)
                    }else{
                        val dialog = showFailedDialog(getString(R.string.failed_update_data_to_database))
                        Handler().postDelayed({
                            dialog.dismiss()
                        }, delayedTime)
                    }
                }
                val dialog = showSuccessDialog(getString(R.string.sucess_update_data_to_database))
                Handler().postDelayed({
                    dialog.dismiss()
                    finish()
                }, 1200)
            }else{
                val dialog = showFailedDialog(getString(R.string.failed_update_data_to_database))
                Handler().postDelayed({
                    dialog.dismiss()
                }, delayedTime)
            }
        }else{
            val result = dbTaskHelper.insert(task?.mainTask)
            if (result > 0){
                if (dataSubTasks != null && dataSubTasks.isNotEmpty()){
                    var isSuccess = false
                    for (subTask: SubTask in dataSubTasks){
                        subTask.idTask = result.toInt()
                        val resultSubTask = dbSubTaskHelper.insert(subTask)
                        isSuccess = resultSubTask > 0
                    }
                    if (isSuccess){
                        val dialog = showSuccessDialog(getString(R.string.sucess_add_data_to_database))
                        Handler().postDelayed({
                            dialog.dismiss()
                        }, 1200)
                    }else{
                        val dialog = showFailedDialog(getString(R.string.failed_add_data_to_database))
                        Handler().postDelayed({
                            dialog.dismiss()
                        }, delayedTime)
                    }
                }
                val dialog = showSuccessDialog(getString(R.string.sucess_add_data_to_database))
                Handler().postDelayed({
                    dialog.dismiss()
                    finish()
                }, 1200)
            }else{
                val dialog = showFailedDialog(getString(R.string.failed_add_data_to_database))
                Handler().postDelayed({
                    dialog.dismiss()
                }, delayedTime)
            }
        }
    }

    private fun showSuccessDialog(desc: String): AlertDialog {
        return AlertDialog.Builder(this)
            .setTitle("Success")
            .setMessage(desc)
            .show()
    }

    private fun showFailedDialog(desc: String): AlertDialog {
        return AlertDialog.Builder(this)
            .setTitle("Failed")
            .setMessage(desc)
            .show()
    }

    private fun checkIsDateFilled(isDateFilled: Boolean) {
        if(isDateFilled){
            btnAddDateTask.background = ContextCompat.getDrawable(this, R.drawable.bg_btn_add_date_task)
            btnAddDateTask.setPadding(24, 24, 24, 24)
            btnRemoveDateTask.visibility = View.VISIBLE
        }else{
            btnAddDateTask.setBackgroundResource(0)
            btnAddDateTask.setPadding(0, 0, 0, 0)
            btnRemoveDateTask.visibility = View.GONE
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(tbNewTask)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isEdit){
            menuInflater.inflate(R.menu.new_task_menu, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_remove_task -> {
                AlertDialog.Builder(this)
                    .setTitle("Delete")
                    .setMessage("Apakah anda yakin akan delete data ini?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        task?.mainTask?.id?.let {
                            val result = dbTaskHelper.deleteTask(it)
                            if (result > 0){
                                val dialogSuccess = showSuccessDialog("Data ini berhasil di delete")
                                Handler().postDelayed({
                                    dialogSuccess.dismiss()
                                    dialog.dismiss()
                                    finish()
                                }, delayedTime)
                            }
                        }
                    }
                    .setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()

            }
        }
        return super.onOptionsItemSelected(item)
    }
}