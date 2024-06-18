package ru.rznnike.eyehealthmanager.app.presentation.app

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import org.mockito.Mockito.only
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.kotlin.mock
import ru.rznnike.eyehealthmanager.app.dispatcher.external.ExternalIntentDispatcher
import ru.rznnike.eyehealthmanager.domain.model.common.ExternalIntentData
import ru.rznnike.eyehealthmanager.domain.model.notification.Notification

class AppPresenterTest : KoinTest {
    private val mockExternalIntentDispatcher: ExternalIntentDispatcher by inject()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single { mock<ExternalIntentDispatcher>() }
            }
        )
    }

    @Test
    fun processNotificationIntent_null_nothing() {
        val presenter = AppPresenter()

        presenter.processNotificationIntent(null)

        verifyNoInteractions(mockExternalIntentDispatcher)
    }

    @Test
    fun processNotificationIntent_notNull_sent() {
        val presenter = AppPresenter()
        val notification = Notification(
            uuid = "",
            title = "",
            message = "",
            externalIntentData = ExternalIntentData.App()
        )

        presenter.processNotificationIntent(notification)

        verify(mockExternalIntentDispatcher, only()).send(notification.externalIntentData)
    }
}