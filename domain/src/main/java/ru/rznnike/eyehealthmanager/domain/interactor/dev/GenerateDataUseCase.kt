package ru.rznnike.eyehealthmanager.domain.interactor.dev

import ru.rznnike.eyehealthmanager.domain.gateway.DevGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseWithParams
import ru.rznnike.eyehealthmanager.domain.model.enums.DataGenerationType

class GenerateDataUseCase(
    private val devGateway: DevGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<DataGenerationType, Unit>(dispatcherProvider.io) {
    override suspend fun execute(parameters: DataGenerationType) =
        devGateway.generateData(parameters)
}
