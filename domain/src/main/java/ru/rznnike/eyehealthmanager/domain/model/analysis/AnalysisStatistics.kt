package ru.rznnike.eyehealthmanager.domain.model.analysis

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnalysisStatistics(
    var visionDynamicType: VisionDynamicType,
    var visionDynamicValue: Int,
    var visionAverageValue: Int,
    var analysisPeriod: Long
) : Parcelable