package ru.rznnike.eyehealthmanager.app.presentation.app

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import ru.rznnike.eyehealthmanager.app.dispatcher.external.ExternalIntentDispatcher
import ru.rznnike.eyehealthmanager.domain.model.ExternalIntentData
import ru.rznnike.eyehealthmanager.domain.model.Notification

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

        verify(mockExternalIntentDispatcher, never()).send(any())
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

        verify(mockExternalIntentDispatcher, times(1)).send(notification.externalIntentData)
    }
}