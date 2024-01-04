package ru.rznnike.eyehealthmanager.app.dispatcher.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import ru.rznnike.eyehealthmanager.domain.global.CoroutineProvider

class EventDispatcherTest {
    @Test
    fun addEventListener_oneListenerAndRightEvent_eventReceived() = runTest {
        // TODO
        val eventDispatcher = EventDispatcher(createTestCoroutineProvider())
    }

    @Test
    fun addEventListener_oneListenerAndWrongEvent_eventNotReceived() = runTest {
        // TODO
    }

    @Test
    fun addEventListener_twoSimilarListeners_eventReceivedByBoth() = runTest {
        // TODO
    }

    @Test
    fun addEventListener_twoDifferentListeners_eventReceivedByOne() = runTest {
        // TODO
    }

    @Test
    fun addSingleByTagEventListener_twoListenersWithSameTag_eventReceivedByLast() = runTest {
        // TODO
    }

    @Test
    fun addSingleByTagEventListener_twoListenersWithDifferentTags_eventReceivedByBoth() = runTest {
        // TODO
    }

    @Test
    fun removeEventListener_existed_removed() = runTest {
        // TODO
    }

    @Test
    fun removeEventListener_notExisted_noError() = runTest {
        // TODO
    }

    private fun CoroutineScope.createTestCoroutineProvider() = object : CoroutineProvider {
        override val scopeIo = this@createTestCoroutineProvider
        override val scopeMain = this@createTestCoroutineProvider
        override val scopeMainImmediate = this@createTestCoroutineProvider
        override val scopeUnconfined = this@createTestCoroutineProvider
    }
}