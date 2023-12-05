package ru.rznnike.eyehealthmanager.domain.model

data class SingleEyeAnalysisResult(
    val chartData: List<EyeChartPoint>,
    val extrapolatedResult: EyeChartPoint?,
    val statistics: AnalysisStatistics?,
    var dynamicCorrections: DynamicCorrectionsData? = null
)