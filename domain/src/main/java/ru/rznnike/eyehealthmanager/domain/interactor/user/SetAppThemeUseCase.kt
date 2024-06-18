package ru.rznnike.eyehealthmanager.domain.interactor.user

import ru.rznnike.eyehealthmanager.domain.gateway.UserGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseWithParams
import ru.rznnike.eyehealthmanager.domain.model.common.AppTheme

class SetAppThemeUseCase(
    private val userGateway: UserGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<AppTheme, Unit>(dispatcherProvider) {
    override suspend fun execute(parameters: AppTheme) =
        userGateway.setAppTheme(parameters)
}