package ru.rznnike.eyehealthmanager.domain.model.analysis

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DynamicCorrectionsData(
    var beginning: Double = 0.0,
    var middle: Double = 0.0,
    var end: Double = 0.0,
    val doctorCorrectionsCalculated: Boolean = false
) : Parcelable