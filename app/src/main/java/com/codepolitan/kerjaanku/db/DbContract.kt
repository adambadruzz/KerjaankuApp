package com.codepolitan.kerjaanku.db

object DbContract {
    class MyTask{
        companion object{
            const val TABLE_TASK = "task"
            const val TASK_ID = "task_id"
            const val TASK_TITLE = "task_title"
            const val TASK_DETAILS = "task_details"
            const val TASK_DATE = "task_date"
            const val TASK_IS_COMPLETE = "task_is_complete"
        }
    }

    class MySuBTask{
        companion object{
            const val TABLE_SUB_TASK = "sub_task"
            const val SUB_TASK_ID = "sub_task_id"
            const val SUB_TASK_TASK_ID = "sub_task_task_id"
            const val SUB_TASK_TITLE = "sub_task_title"
            const val SUB_TASK_IS_COMPLETE = "sub_task_is_complete"
        }
    }
}