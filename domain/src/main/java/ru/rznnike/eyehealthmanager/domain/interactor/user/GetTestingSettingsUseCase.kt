package ru.rznnike.eyehealthmanager.domain.interactor.user

import ru.rznnike.eyehealthmanager.domain.gateway.UserGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCase
import ru.rznnike.eyehealthmanager.domain.model.TestingSettings

class GetTestingSettingsUseCase(
    private val userGateway: UserGateway,
    dispatcherProvider: DispatcherProvider
) : UseCase<TestingSettings>(dispatcherProvider.io) {
    override suspend fun execute() =
        userGateway.getTestingSettings()
}
