package ru.rznnike.eyehealthmanager.data.gateway

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import ru.rznnike.eyehealthmanager.domain.gateway.NotificationGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.model.notification.CancelNotification
import ru.rznnike.eyehealthmanager.domain.model.notification.Notification

class NotificationGatewayImpl(
    private val dispatcherProvider: DispatcherProvider
) : NotificationGateway {
    private val notificationFlow = MutableSharedFlow<Notification>()
    private val cancelNotificationFlow = MutableSharedFlow<CancelNotification>()

    override suspend fun emitShowNotification(notification: Notification) = withContext(dispatcherProvider.default) {
        notificationFlow.emit(notification)
    }

    override suspend fun emitCancelNotification(cancelNotification: CancelNotification) = withContext(dispatcherProvider.default) {
        cancelNotificationFlow.emit(cancelNotification)
    }

    override suspend fun observeCancelNotification(): SharedFlow<CancelNotification> = withContext(dispatcherProvider.io) {
        cancelNotificationFlow.asSharedFlow()
    }

    override suspend fun observeShowNotification(): SharedFlow<Notification> = withContext(dispatcherProvider.io) {
        notificationFlow.asSharedFlow()
    }
}