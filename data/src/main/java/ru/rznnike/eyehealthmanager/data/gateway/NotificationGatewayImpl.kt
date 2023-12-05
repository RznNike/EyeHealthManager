package ru.rznnike.eyehealthmanager.data.gateway

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import ru.rznnike.eyehealthmanager.domain.gateway.NotificationGateway
import ru.rznnike.eyehealthmanager.domain.model.CancelNotification
import ru.rznnike.eyehealthmanager.domain.model.Notification

class NotificationGatewayImpl : NotificationGateway {
    private val notificationFlow = MutableSharedFlow<Notification>()
    private val cancelNotificationFlow = MutableSharedFlow<CancelNotification>()

    override suspend fun emitShowNotification(notification: Notification) =
        notificationFlow.emit(notification)

    override suspend fun emitCancelNotification(cancelNotification: CancelNotification) =
        cancelNotificationFlow.emit(cancelNotification)

    override suspend fun observeCancelNotification() =
        cancelNotificationFlow.asSharedFlow()

    override suspend fun observeShowNotification() =
        notificationFlow.asSharedFlow()
}