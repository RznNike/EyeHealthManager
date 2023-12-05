package ru.rznnike.eyehealthmanager.device.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.launch
import ru.rznnike.eyehealthmanager.domain.global.CoroutineProvider
import org.koin.android.ext.android.inject
import ru.rznnike.eyehealthmanager.domain.interactor.notification.EmitShowNotificationUseCase
import ru.rznnike.eyehealthmanager.domain.model.toNotification

class AppFirebaseMessagingService : FirebaseMessagingService() {
    private val coroutineProvider: CoroutineProvider by inject()
    private val emitShowNotificationUseCase: EmitShowNotificationUseCase by inject()

    override fun onNewToken(token: String) = Unit

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        coroutineProvider.scopeIo.launch {
            val notification = remoteMessage.data.toNotification(
                title = remoteMessage.notification?.title,
                message = remoteMessage.notification?.body
            )
            emitShowNotificationUseCase(notification)
        }
    }
}
