package com.codepolitan.kerjaanku.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task(
    var mainTask: MainTask? = null,
    var subTasks: List<SubTask>? = null
): Parcelable