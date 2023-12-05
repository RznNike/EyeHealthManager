package ru.rznnike.eyehealthmanager.domain.gateway

import ru.rznnike.eyehealthmanager.domain.model.AnalysisParams
import ru.rznnike.eyehealthmanager.domain.model.AnalysisResult

interface AnalysisGateway {
    suspend fun getAnalysisResult(params: AnalysisParams): AnalysisResult
}
