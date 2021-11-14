package com.codepolitan.kerjaanku.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.codepolitan.kerjaanku.db.DbContract.MyTask.Companion.TABLE_TASK
import com.codepolitan.kerjaanku.db.DbContract.MyTask.Companion.TASK_DATE
import com.codepolitan.kerjaanku.db.DbContract.MyTask.Companion.TASK_DETAILS
import com.codepolitan.kerjaanku.db.DbContract.MyTask.Companion.TASK_ID
import com.codepolitan.kerjaanku.db.DbContract.MyTask.Companion.TASK_IS_COMPLETE
import com.codepolitan.kerjaanku.db.DbContract.MyTask.Companion.TASK_TITLE
import com.codepolitan.kerjaanku.model.MainTask

class DbTaskHelper (context: Context?){
    companion object{
        private const val TABLE_NAME = TABLE_TASK
        private lateinit var dbHelper: DbHelper
        private lateinit var database: SQLiteDatabase
        private var instance: DbTaskHelper? = null

        fun getInstance(context: Context?): DbTaskHelper{
            if (instance == null){
                synchronized(SQLiteOpenHelper::class){
                    if (instance == null){
                        instance = DbTaskHelper(context)
                    }
                }
            }
            return instance as DbTaskHelper
        }
    }

    init {
        dbHelper = DbHelper(context)
    }

    private fun open(){
        database = dbHelper.writableDatabase
    }

    private fun close(){
        dbHelper.close()

        if (database.isOpen){
            database.close()
        }
    }

    fun insert(mainTask: MainTask?): Long{
        open()
        val values = ContentValues()
        values.put(TASK_TITLE, mainTask?.title)
        values.put(TASK_DETAILS, mainTask?.details)
        values.put(TASK_DATE, mainTask?.date)
        values.put(TASK_IS_COMPLETE, mainTask?.isComplete)

        val result = database.insert(TABLE_NAME, null, values)
        close()
        return result
    }

    fun getAllTask(): List<MainTask>?{
        open()
        val tasks = mutableListOf<MainTask>()
        val query = "SELECT * FROM $TABLE_NAME WHERE $TASK_IS_COMPLETE = 0"
        val cursor = database.rawQuery(query, null)
        if (cursor != null){
            while (cursor.moveToNext()){
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(TASK_TITLE))
                val details = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DETAILS))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DATE))
                val isComplete = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_IS_COMPLETE))

                val isCompleteTask: Boolean
                isCompleteTask = isComplete == 1
                tasks.add(MainTask(id = id, title = title, details = details, date = date, isComplete = isCompleteTask))
            }
        }else{
            return null
        }

        cursor.close()
        close()
        return tasks
    }

    fun updateTask(mainTask: MainTask?): Int{
        open()
        val values = ContentValues()
        values.put(TASK_TITLE, mainTask?.title)
        values.put(TASK_DETAILS, mainTask?.details)
        values.put(TASK_DATE, mainTask?.date)
        if (mainTask!!.isComplete){
            values.put(TASK_IS_COMPLETE, 1)
        }else{
            values.put(TASK_IS_COMPLETE, 0)
        }
        val result = database.update(TABLE_NAME, values, "$TASK_ID = ${mainTask?.id}", null)
        close()
        return result
    }

    fun deleteTask(id: Int): Int{
        open()
        val result = database.delete(TABLE_NAME, "$TASK_ID = $id", null)
        close()
        return result
    }

    fun deleteAllTaskComplete(): Int{
        open()
        val result = database.delete(TABLE_NAME, "$TASK_IS_COMPLETE = 1", null)
        close()
        return result
    }

    fun getAllTaskComplete(): List<MainTask>?{
        open()
        val tasks = mutableListOf<MainTask>()
        val query = "SELECT * FROM $TABLE_NAME WHERE $TASK_IS_COMPLETE = 1"
        val cursor = database.rawQuery(query, null)
        if (cursor != null){
            while (cursor.moveToNext()){
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(TASK_TITLE))
                val details = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DETAILS))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DATE))
                val isComplete = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_IS_COMPLETE))

                val isCompleteTask: Boolean
                isCompleteTask = isComplete == 1
                tasks.add(MainTask(id = id, title = title, details = details, date = date, isComplete = isCompleteTask))
            }
        }else{
            return null
        }

        cursor.close()
        close()
        return tasks
    }
}