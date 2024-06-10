package ru.rznnike.eyehealthmanager.app.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.launch
import ru.rznnike.eyehealthmanager.domain.global.CoroutineScopeProvider
import org.koin.android.ext.android.inject
import ru.rznnike.eyehealthmanager.domain.interactor.notification.EmitShowNotificationUseCase
import ru.rznnike.eyehealthmanager.domain.model.notification.toNotification

class AppFirebaseMessagingService : FirebaseMessagingService() {
    private val coroutineScopeProvider: CoroutineScopeProvider by inject()
    private val emitShowNotificationUseCase: EmitShowNotificationUseCase by inject()

    override fun onNewToken(token: String) = Unit

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        coroutineScopeProvider.io.launch {
            val notification = remoteMessage.data.toNotification(
                title = remoteMessage.notification?.title,
                message = remoteMessage.notification?.body
            )
            emitShowNotificationUseCase(notification)
        }
    }
}
