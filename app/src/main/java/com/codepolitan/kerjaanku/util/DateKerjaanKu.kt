package com.codepolitan.kerjaanku.util

import android.app.DatePickerDialog
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

object DateKerjaanKu {

    fun showDatePicker(context: Context, onDateSetListener: DatePickerDialog.OnDateSetListener){
        //Get current date
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(context, onDateSetListener, year, month, day).show()
    }

    fun dateFormatSql(year: Int, month: Int, dayOfMonth: Int): String{
        return "$year-$month-$dayOfMonth"
    }

    fun dateFromSqlToDateViewTask(rawDate: String): String{
        var result = ""
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(rawDate)

        if (date != null){
            result = SimpleDateFormat("EEE, dd MM yyyy", Locale.getDefault()).format(date)
        }

        return result
    }
}