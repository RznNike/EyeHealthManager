package ru.rznnike.eyehealthmanager.app.presentation.app

import moxy.InjectViewState
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.app.dispatcher.external.ExternalIntentDispatcher
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.domain.model.Notification

@InjectViewState
class AppPresenter : BasePresenter<AppView>() {
    private val externalIntentDispatcher: ExternalIntentDispatcher by inject()

    fun processNotificationIntent(notification: Notification?) {
        notification?.let {
            externalIntentDispatcher.send(notification.externalIntentData)
        }
    }
}
