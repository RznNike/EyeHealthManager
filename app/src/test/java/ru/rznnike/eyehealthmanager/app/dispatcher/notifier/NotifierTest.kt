package ru.rznnike.eyehealthmanager.app.dispatcher.notifier

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.rznnike.eyehealthmanager.app.utils.createTestCoroutineProvider

@OptIn(ExperimentalCoroutinesApi::class)
class NotifierTest {
    @Test
    fun sendMessageText_noMessages_nothingReceived() = runTest {
        val notifier = Notifier(createTestCoroutineProvider())
        val receivedMessages = mutableListOf<SystemMessage>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            notifier.subscribe().collect { message ->
                receivedMessages.add(message)
            }
        }

        assertTrue(receivedMessages.isEmpty())
    }

    @Test
    fun sendMessageText_oneMessage_received() = runTest {
        val notifier = Notifier(createTestCoroutineProvider())
        val receivedMessages = mutableListOf<SystemMessage>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            notifier.subscribe().collect { message ->
                receivedMessages.add(message)
            }
        }
        notifier.sendMessage(
            text = "123",
            level = SystemMessage.Level.ERROR,
            showOnTop = true
        )
        testScheduler.advanceUntilIdle()

        assertEquals(1, receivedMessages.size)
        assertEquals("123", receivedMessages.first().text)
        assertNull(receivedMessages.first().textRes)
        assertEquals(SystemMessage.Type.BAR, receivedMessages.first().type)
        assertEquals(SystemMessage.Level.ERROR, receivedMessages.first().level)
        assertEquals(true, receivedMessages.first().showOnTop)
        assertNull(receivedMessages.first().actionText)
        assertNull(receivedMessages.first().actionTextRes)
        assertNull(receivedMessages.first().actionCallback)
    }

    @Test
    fun sendMessageText_twoMessages_allReceived() = runTest {
        val notifier = Notifier(createTestCoroutineProvider())
        val receivedMessages = mutableListOf<SystemMessage>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            notifier.subscribe().collect { message ->
                receivedMessages.add(message)
            }
        }
        notifier.sendMessage(
            text = "123",
            level = SystemMessage.Level.ERROR,
            showOnTop = true
        )
        testScheduler.advanceUntilIdle()
        notifier.sendMessage(
            text = "456",
            level = SystemMessage.Level.NORMAL,
            showOnTop = false
        )
        testScheduler.advanceUntilIdle()

        assertEquals(2, receivedMessages.size)
        assertEquals("123", receivedMessages.first().text)
        assertNull(receivedMessages.first().textRes)
        assertEquals(SystemMessage.Type.BAR, receivedMessages.first().type)
        assertEquals(SystemMessage.Level.ERROR, receivedMessages.first().level)
        assertEquals(true, receivedMessages.first().showOnTop)
        assertNull(receivedMessages.first().actionText)
        assertNull(receivedMessages.first().actionTextRes)
        assertNull(receivedMessages.first().actionCallback)
        assertEquals("456", receivedMessages[1].text)
        assertNull(receivedMessages[1].textRes)
        assertEquals(SystemMessage.Type.BAR, receivedMessages[1].type)
        assertEquals(SystemMessage.Level.NORMAL, receivedMessages[1].level)
        assertEquals(false, receivedMessages[1].showOnTop)
        assertNull(receivedMessages[1].actionText)
        assertNull(receivedMessages[1].actionTextRes)
        assertNull(receivedMessages[1].actionCallback)
    }

    @Test
    fun sendMessageText_lateSubscription_nothingReceived() = runTest {
        val notifier = Notifier(createTestCoroutineProvider())
        val receivedMessages = mutableListOf<SystemMessage>()

        notifier.sendMessage(
            text = "123",
            level = SystemMessage.Level.ERROR,
            showOnTop = true
        )
        testScheduler.advanceUntilIdle()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            notifier.subscribe().collect { message ->
                receivedMessages.add(message)
            }
        }

        assertTrue(receivedMessages.isEmpty())
    }

    @Test
    fun sendMessageRes_oneMessage_received() = runTest {
        val notifier = Notifier(createTestCoroutineProvider())
        val receivedMessages = mutableListOf<SystemMessage>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            notifier.subscribe().collect { message ->
                receivedMessages.add(message)
            }
        }
        notifier.sendMessage(
            textRes = 42,
            level = SystemMessage.Level.ERROR,
            showOnTop = true
        )
        testScheduler.advanceUntilIdle()

        assertEquals(1, receivedMessages.size)
        assertNull(receivedMessages.first().text)
        assertEquals(42, receivedMessages.first().textRes)
        assertEquals(SystemMessage.Type.BAR, receivedMessages.first().type)
        assertEquals(SystemMessage.Level.ERROR, receivedMessages.first().level)
        assertEquals(true, receivedMessages.first().showOnTop)
        assertNull(receivedMessages.first().actionText)
        assertNull(receivedMessages.first().actionTextRes)
        assertNull(receivedMessages.first().actionCallback)
    }

    @Test
    fun sendAlertText_oneMessage_received() = runTest {
        val notifier = Notifier(createTestCoroutineProvider())
        val receivedMessages = mutableListOf<SystemMessage>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            notifier.subscribe().collect { message ->
                receivedMessages.add(message)
            }
        }
        notifier.sendAlert(
            text = "123"
        )
        testScheduler.advanceUntilIdle()

        assertEquals(1, receivedMessages.size)
        assertEquals("123", receivedMessages.first().text)
        assertNull(receivedMessages.first().textRes)
        assertEquals(SystemMessage.Type.ALERT, receivedMessages.first().type)
        assertEquals(SystemMessage.Level.NORMAL, receivedMessages.first().level)
        assertEquals(false, receivedMessages.first().showOnTop)
        assertNull(receivedMessages.first().actionText)
        assertNull(receivedMessages.first().actionTextRes)
        assertNull(receivedMessages.first().actionCallback)
    }

    @Test
    fun sendAlertRes_oneMessage_received() = runTest {
        val notifier = Notifier(createTestCoroutineProvider())
        val receivedMessages = mutableListOf<SystemMessage>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            notifier.subscribe().collect { message ->
                receivedMessages.add(message)
            }
        }
        notifier.sendAlert(
            textRes = 42
        )
        testScheduler.advanceUntilIdle()

        assertEquals(1, receivedMessages.size)
        assertNull(receivedMessages.first().text)
        assertEquals(42, receivedMessages.first().textRes)
        assertEquals(SystemMessage.Type.ALERT, receivedMessages.first().type)
        assertEquals(SystemMessage.Level.NORMAL, receivedMessages.first().level)
        assertEquals(false, receivedMessages.first().showOnTop)
        assertNull(receivedMessages.first().actionText)
        assertNull(receivedMessages.first().actionTextRes)
        assertNull(receivedMessages.first().actionCallback)
    }

    @Test
    fun sendActionMessageText_oneMessage_received() = runTest {
        val notifier = Notifier(createTestCoroutineProvider())
        val actionCallback = { }
        val receivedMessages = mutableListOf<SystemMessage>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            notifier.subscribe().collect { message ->
                receivedMessages.add(message)
            }
        }
        notifier.sendActionMessage(
            text = "123",
            actionText = "456",
            showOnTop = true,
            actionCallback = actionCallback
        )
        testScheduler.advanceUntilIdle()

        assertEquals(1, receivedMessages.size)
        assertEquals("123", receivedMessages.first().text)
        assertNull(receivedMessages.first().textRes)
        assertEquals(SystemMessage.Type.BAR, receivedMessages.first().type)
        assertEquals(SystemMessage.Level.NORMAL, receivedMessages.first().level)
        assertEquals(true, receivedMessages.first().showOnTop)
        assertEquals("456", receivedMessages.first().actionText)
        assertNull(receivedMessages.first().actionTextRes)
        assertEquals(actionCallback, receivedMessages.first().actionCallback)
    }

    @Test
    fun sendActionMessageRes_oneMessage_received() = runTest {
        val notifier = Notifier(createTestCoroutineProvider())
        val actionCallback = { }
        val receivedMessages = mutableListOf<SystemMessage>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            notifier.subscribe().collect { message ->
                receivedMessages.add(message)
            }
        }
        notifier.sendActionMessage(
            textRes = 42,
            actionTextRes = 24,
            showOnTop = false,
            actionCallback = actionCallback
        )
        testScheduler.advanceUntilIdle()

        assertEquals(1, receivedMessages.size)
        assertNull(receivedMessages.first().text)
        assertEquals(42, receivedMessages.first().textRes)
        assertEquals(SystemMessage.Type.BAR, receivedMessages.first().type)
        assertEquals(SystemMessage.Level.NORMAL, receivedMessages.first().level)
        assertEquals(false, receivedMessages.first().showOnTop)
        assertNull(receivedMessages.first().actionText)
        assertEquals(24, receivedMessages.first().actionTextRes)
        assertEquals(actionCallback, receivedMessages.first().actionCallback)
    }
}