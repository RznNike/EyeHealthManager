package ru.rznnike.eyehealthmanager.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.rznnike.eyehealthmanager.domain.model.enums.VisionDynamicType

@Parcelize
data class AnalysisStatistics(
    var visionDynamicType: VisionDynamicType,
    var visionDynamicValue: Int,
    var visionAverageValue: Int,
    var analysisPeriod: Long
) : Parcelable