package ru.rznnike.eyehealthmanager.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EyeChartPoint(
    val timestamp: Long = 0,
    val value: Int = 0
) : Parcelable