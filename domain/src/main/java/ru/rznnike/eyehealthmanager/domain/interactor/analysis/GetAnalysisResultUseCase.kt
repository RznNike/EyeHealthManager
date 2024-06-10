package ru.rznnike.eyehealthmanager.domain.interactor.analysis

import ru.rznnike.eyehealthmanager.domain.gateway.AnalysisGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseWithParams
import ru.rznnike.eyehealthmanager.domain.model.analysis.AnalysisParameters
import ru.rznnike.eyehealthmanager.domain.model.analysis.AnalysisResult

class GetAnalysisResultUseCase(
    private val analysisGateway: AnalysisGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<AnalysisParameters, AnalysisResult>(dispatcherProvider) {
    override suspend fun execute(parameters: AnalysisParameters) =
        analysisGateway.getAnalysisResult(parameters)
}
