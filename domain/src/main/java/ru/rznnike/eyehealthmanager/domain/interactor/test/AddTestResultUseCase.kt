package ru.rznnike.eyehealthmanager.domain.interactor.test

import ru.rznnike.eyehealthmanager.domain.gateway.TestGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseWithParams
import ru.rznnike.eyehealthmanager.domain.model.test.TestResult

class AddTestResultUseCase(
    private val testGateway: TestGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<TestResult, Long>(dispatcherProvider) {
    override suspend fun execute(parameters: TestResult) =
        testGateway.addTestResult(parameters)
}
