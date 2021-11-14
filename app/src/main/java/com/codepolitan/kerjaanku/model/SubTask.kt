package com.codepolitan.kerjaanku.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubTask(
    val id: Int? = null,
    var idTask: Int? = null,
    var title: String? = null,
    var isComplete: Boolean = false
): Parcelable