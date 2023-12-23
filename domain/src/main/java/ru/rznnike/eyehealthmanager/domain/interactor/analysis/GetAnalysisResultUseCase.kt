package ru.rznnike.eyehealthmanager.domain.interactor.analysis

import ru.rznnike.eyehealthmanager.domain.gateway.AnalysisGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseWithParams
import ru.rznnike.eyehealthmanager.domain.model.AnalysisParameters
import ru.rznnike.eyehealthmanager.domain.model.AnalysisResult

class GetAnalysisResultUseCase(
    private val analysisGateway: AnalysisGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<AnalysisParameters, AnalysisResult>(dispatcherProvider.io) {
    override suspend fun execute(parameters: AnalysisParameters) =
        analysisGateway.getAnalysisResult(parameters)
}
