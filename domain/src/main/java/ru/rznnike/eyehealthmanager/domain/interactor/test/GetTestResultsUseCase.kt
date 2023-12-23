package ru.rznnike.eyehealthmanager.domain.interactor.test

import ru.rznnike.eyehealthmanager.domain.gateway.TestGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseWithParams
import ru.rznnike.eyehealthmanager.domain.model.TestResult
import ru.rznnike.eyehealthmanager.domain.model.TestResultPagingParameters

class GetTestResultsUseCase(
    private val testGateway: TestGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<TestResultPagingParameters, List<TestResult>>(dispatcherProvider.io) {
    override suspend fun execute(parameters: TestResultPagingParameters) =
        testGateway.getTestResults(parameters)
}
