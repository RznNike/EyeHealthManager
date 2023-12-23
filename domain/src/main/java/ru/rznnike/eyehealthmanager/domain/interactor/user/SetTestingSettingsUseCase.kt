package ru.rznnike.eyehealthmanager.domain.interactor.user

import ru.rznnike.eyehealthmanager.domain.gateway.UserGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseWithParams
import ru.rznnike.eyehealthmanager.domain.model.TestingSettings

class SetTestingSettingsUseCase(
    private val userGateway: UserGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<TestingSettings, Unit>(dispatcherProvider.io) {
    override suspend fun execute(parameters: TestingSettings) =
        userGateway.setTestingSettings(parameters)
}
