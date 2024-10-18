package ru.rznnike.eyehealthmanager.domain.interactor.test

import ru.rznnike.eyehealthmanager.domain.gateway.TestGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseWithParams

class DeleteTestResultUseCase(
    private val testGateway: TestGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<Long, Unit>(dispatcherProvider) {
    override suspend fun execute(parameters: Long) =
        testGateway.deleteTestResultById(parameters)
}
