package ru.rznnike.eyehealthmanager.domain.model.analysis

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.rznnike.eyehealthmanager.domain.model.analysis.AnalysisStatistics
import ru.rznnike.eyehealthmanager.domain.model.analysis.DynamicCorrectionsData
import ru.rznnike.eyehealthmanager.domain.model.analysis.EyeChartPoint

@Parcelize
data class SingleEyeAnalysisResult(
    val chartData: List<EyeChartPoint>,
    val extrapolatedResult: EyeChartPoint?,
    val statistics: AnalysisStatistics?,
    var dynamicCorrections: DynamicCorrectionsData? = null
) : Parcelable