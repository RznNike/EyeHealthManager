package ru.rznnike.eyehealthmanager.domain.interactor.test

import ru.rznnike.eyehealthmanager.domain.gateway.TestGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCase

class DeleteDuplicatesUseCase(
    private val testGateway: TestGateway,
    dispatcherProvider: DispatcherProvider
) : UseCase<Unit>(dispatcherProvider.io) {
    override suspend fun execute() =
        testGateway.deleteDuplicates()
}
