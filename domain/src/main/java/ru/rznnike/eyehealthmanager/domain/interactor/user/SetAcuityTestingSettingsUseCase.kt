package ru.rznnike.eyehealthmanager.domain.interactor.user

import ru.rznnike.eyehealthmanager.domain.gateway.UserGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseWithParams
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestingSettings

class SetAcuityTestingSettingsUseCase(
    private val userGateway: UserGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<AcuityTestingSettings, Unit>(dispatcherProvider) {
    override suspend fun execute(parameters: AcuityTestingSettings) =
        userGateway.setAcuityTestingSettings(parameters)
}
