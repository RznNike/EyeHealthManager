package ru.rznnike.eyehealthmanager.domain.interactor.user

import ru.rznnike.eyehealthmanager.domain.gateway.UserGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCase
import ru.rznnike.eyehealthmanager.domain.model.AcuityTestingSettings

class GetAcuityTestingSettingsUseCase(
    private val userGateway: UserGateway,
    dispatcherProvider: DispatcherProvider
) : UseCase<AcuityTestingSettings>(dispatcherProvider.io) {
    override suspend fun execute() =
        userGateway.getAcuityTestingSettings()
}
