package ru.rznnike.eyehealthmanager.domain.interactor.user

import ru.rznnike.eyehealthmanager.domain.gateway.UserGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseWithParams

class SetDisplayedChangelogVersionUseCase(
    private val userGateway: UserGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<Int, Unit>(dispatcherProvider.io) {
    override suspend fun execute(parameters: Int) =
        userGateway.setDisplayedChangelogVersion(parameters)
}
