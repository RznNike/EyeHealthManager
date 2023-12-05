package ru.rznnike.eyehealthmanager.domain.interactor.notification

import ru.rznnike.eyehealthmanager.domain.gateway.NotificationGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.FlowUseCase
import ru.rznnike.eyehealthmanager.domain.model.CancelNotification

class ObserveCancelNotificationUseCase(
    private val notificationGateway: NotificationGateway,
    dispatcherProvider: DispatcherProvider
) : FlowUseCase<CancelNotification>(dispatcherProvider.io) {
    override suspend fun execute() =
        notificationGateway.observeCancelNotification()
}