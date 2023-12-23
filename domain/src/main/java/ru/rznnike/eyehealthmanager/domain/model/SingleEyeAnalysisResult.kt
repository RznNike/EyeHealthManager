package ru.rznnike.eyehealthmanager.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SingleEyeAnalysisResult(
    val chartData: List<EyeChartPoint>,
    val extrapolatedResult: EyeChartPoint?,
    val statistics: AnalysisStatistics?,
    var dynamicCorrections: DynamicCorrectionsData? = null
) : Parcelable