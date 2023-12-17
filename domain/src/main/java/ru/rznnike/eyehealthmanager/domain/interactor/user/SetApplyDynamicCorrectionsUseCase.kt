package ru.rznnike.eyehealthmanager.domain.interactor.user

import ru.rznnike.eyehealthmanager.domain.gateway.UserGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseWithParams

class SetApplyDynamicCorrectionsUseCase(
    private val userGateway: UserGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<Boolean, Unit>(dispatcherProvider.io) {
    override suspend fun execute(parameters: Boolean) =
        userGateway.setApplyDynamicCorrections(parameters)
}
