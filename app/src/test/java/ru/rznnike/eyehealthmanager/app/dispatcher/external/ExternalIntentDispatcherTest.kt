package ru.rznnike.eyehealthmanager.app.dispatcher.external

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.rznnike.eyehealthmanager.app.utils.createTestCoroutineProvider
import ru.rznnike.eyehealthmanager.domain.model.common.ExternalIntentData

@OptIn(ExperimentalCoroutinesApi::class)
class ExternalIntentDispatcherTest {
    @Test
    fun subscribe_noIntents_defaultProcessedIntentReceived() = runTest {
        val externalIntentDispatcher = ExternalIntentDispatcher(createTestCoroutineProvider())
        val receivedIntents = mutableListOf<ExternalIntentData>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            externalIntentDispatcher.subscribe().collect { intentData ->
                receivedIntents.add(intentData)
            }
        }

        assertEquals(1, receivedIntents.size)
        assertTrue(receivedIntents.first() is ExternalIntentData.App)
        assertTrue((receivedIntents.first() as ExternalIntentData.App).processed)
    }

    @Test
    fun subscribe_intentSent_allReceived() = runTest {
        val externalIntentDispatcher = ExternalIntentDispatcher(createTestCoroutineProvider())
        val intent = ExternalIntentData.App()
        val receivedIntents = mutableListOf<ExternalIntentData>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            externalIntentDispatcher.subscribe().collect { intentData ->
                receivedIntents.add(intentData)
            }
        }
        externalIntentDispatcher.send(intent)
        testScheduler.advanceUntilIdle()


        assertEquals(2, receivedIntents.size)
        assertTrue(receivedIntents[1] is ExternalIntentData.App)
        assertFalse((receivedIntents[1] as ExternalIntentData.App).processed)
    }

    @Test
    fun subscribe_lateSubscription_onlyLastIntentReceived() = runTest {
        val externalIntentDispatcher = ExternalIntentDispatcher(createTestCoroutineProvider())
        val intent = ExternalIntentData.App()
        val receivedIntents = mutableListOf<ExternalIntentData>()

        externalIntentDispatcher.send(intent)
        testScheduler.advanceUntilIdle()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            externalIntentDispatcher.subscribe().collect { intentData ->
                receivedIntents.add(intentData)
            }
        }

        assertEquals(1, receivedIntents.size)
        assertTrue(receivedIntents.first() is ExternalIntentData.App)
        assertFalse((receivedIntents.first() as ExternalIntentData.App).processed)
    }
}