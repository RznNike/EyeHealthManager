package ru.rznnike.eyehealthmanager.domain.interactor.user

import ru.rznnike.eyehealthmanager.domain.gateway.UserGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCase
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestingSettings

class GetAcuityTestingSettingsUseCase(
    private val userGateway: UserGateway,
    dispatcherProvider: DispatcherProvider
) : UseCase<AcuityTestingSettings>(dispatcherProvider) {
    override suspend fun execute() =
        userGateway.getAcuityTestingSettings()
}
