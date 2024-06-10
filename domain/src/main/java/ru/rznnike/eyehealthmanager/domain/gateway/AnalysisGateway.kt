package ru.rznnike.eyehealthmanager.domain.gateway

import ru.rznnike.eyehealthmanager.domain.model.analysis.AnalysisParameters
import ru.rznnike.eyehealthmanager.domain.model.analysis.AnalysisResult

interface AnalysisGateway {
    suspend fun getAnalysisResult(parameters: AnalysisParameters): AnalysisResult
}
