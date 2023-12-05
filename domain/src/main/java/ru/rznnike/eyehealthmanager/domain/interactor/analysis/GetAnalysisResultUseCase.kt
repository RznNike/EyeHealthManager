package ru.rznnike.eyehealthmanager.domain.interactor.analysis

import ru.rznnike.eyehealthmanager.domain.gateway.AnalysisGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseWithParams
import ru.rznnike.eyehealthmanager.domain.model.AnalysisParams
import ru.rznnike.eyehealthmanager.domain.model.AnalysisResult

class GetAnalysisResultUseCase(
    private val analysisGateway: AnalysisGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<AnalysisParams, AnalysisResult>(dispatcherProvider.io) {
    override suspend fun execute(parameters: AnalysisParams) =
        analysisGateway.getAnalysisResult(parameters)
}
