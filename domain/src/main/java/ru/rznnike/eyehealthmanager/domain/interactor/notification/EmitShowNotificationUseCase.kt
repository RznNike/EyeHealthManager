package ru.rznnike.eyehealthmanager.domain.interactor.notification

import ru.rznnike.eyehealthmanager.domain.gateway.NotificationGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseWithParams
import ru.rznnike.eyehealthmanager.domain.model.notification.Notification

class EmitShowNotificationUseCase(
    private val notificationGateway: NotificationGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<Notification, Unit>(dispatcherProvider) {
    override suspend fun execute(parameters: Notification) =
        notificationGateway.emitShowNotification(parameters)
}