package ru.rznnike.eyehealthmanager.domain.gateway

import kotlinx.coroutines.flow.Flow
import ru.rznnike.eyehealthmanager.domain.model.notification.CancelNotification
import ru.rznnike.eyehealthmanager.domain.model.notification.Notification

interface NotificationGateway {
    suspend fun emitShowNotification(notification: Notification)

    suspend fun emitCancelNotification(cancelNotification: CancelNotification)

    suspend fun observeCancelNotification(): Flow<CancelNotification>

    suspend fun observeShowNotification(): Flow<Notification>
}