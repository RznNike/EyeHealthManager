package ru.rznnike.eyehealthmanager.domain.gateway

import ru.rznnike.eyehealthmanager.domain.model.AnalysisParameters
import ru.rznnike.eyehealthmanager.domain.model.AnalysisResult

interface AnalysisGateway {
    suspend fun getAnalysisResult(parameters: AnalysisParameters): AnalysisResult
}
