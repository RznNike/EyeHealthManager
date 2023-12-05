package ru.rznnike.eyehealthmanager.domain.model

import ru.rznnike.eyehealthmanager.domain.model.enums.VisionDynamicType

data class AnalysisStatistics(
    var visionDynamicType: VisionDynamicType,
    var visionDynamicValue: Int,
    var visionAverageValue: Int,
    var visionAnalysisPeriod: Long
)