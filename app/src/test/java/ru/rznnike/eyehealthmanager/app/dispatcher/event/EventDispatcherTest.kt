package ru.rznnike.eyehealthmanager.app.dispatcher.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import ru.rznnike.eyehealthmanager.domain.global.CoroutineProvider

class EventDispatcherTest {
    @Test
    fun addEventListener_oneListenerAndRightEvent_eventReceived() = runTest {
        val eventDispatcher = EventDispatcher(createTestCoroutineProvider())
        val mockListener = mock(EventDispatcher.EventListener::class.java)
        val event = AppEvent.JournalChanged

        eventDispatcher.addEventListener(AppEvent.JournalChanged::class, mockListener)
        eventDispatcher.sendEvent(event)
        testScheduler.advanceUntilIdle()

        verify(mockListener, times(1)).onEvent(event)
    }

    @Test
    fun addEventListener_oneListenerAndWrongEvent_eventNotReceived() = runTest {
        val eventDispatcher = EventDispatcher(createTestCoroutineProvider())
        val mockListener = mock(EventDispatcher.EventListener::class.java)
        val event = AppEvent.JournalDuplicatesDeletionRequested

        eventDispatcher.addEventListener(AppEvent.JournalChanged::class, mockListener)
        eventDispatcher.sendEvent(event)
        testScheduler.advanceUntilIdle()

        verify(mockListener, never()).onEvent(event)
    }

    @Test
    fun addEventListener_twoSimilarListeners_eventReceivedByBoth() = runTest {
        val eventDispatcher = EventDispatcher(createTestCoroutineProvider())
        val mockListener1 = mock(EventDispatcher.EventListener::class.java)
        val mockListener2 = mock(EventDispatcher.EventListener::class.java)
        val event = AppEvent.JournalChanged

        eventDispatcher.addEventListener(AppEvent.JournalChanged::class, mockListener1)
        eventDispatcher.addEventListener(AppEvent.JournalChanged::class, mockListener2)
        eventDispatcher.sendEvent(event)
        testScheduler.advanceUntilIdle()

        verify(mockListener1, times(1)).onEvent(event)
        verify(mockListener2, times(1)).onEvent(event)
    }

    @Test
    fun addEventListener_twoDifferentListeners_eventReceivedByOne() = runTest {
        val eventDispatcher = EventDispatcher(createTestCoroutineProvider())
        val mockListener1 = mock(EventDispatcher.EventListener::class.java)
        val mockListener2 = mock(EventDispatcher.EventListener::class.java)
        val event = AppEvent.JournalChanged

        eventDispatcher.addEventListener(AppEvent.JournalChanged::class, mockListener1)
        eventDispatcher.addEventListener(AppEvent.JournalDuplicatesDeletionRequested::class, mockListener2)
        eventDispatcher.sendEvent(event)
        testScheduler.advanceUntilIdle()

        verify(mockListener1, times(1)).onEvent(event)
        verify(mockListener2, never()).onEvent(event)
    }

    @Test
    fun addSingleByTagEventListener_twoListenersWithSameTag_eventReceivedByLast() = runTest {
        val eventDispatcher = EventDispatcher(createTestCoroutineProvider())
        val mockListener1 = mock(EventDispatcher.EventListener::class.java)
        `when`(mockListener1.getTag()).thenReturn("testTag")
        val mockListener2 = mock(EventDispatcher.EventListener::class.java)
        `when`(mockListener2.getTag()).thenReturn("testTag")
        val event = AppEvent.JournalChanged

        eventDispatcher.addSingleByTagEventListener(AppEvent.JournalChanged::class, mockListener1)
        eventDispatcher.addSingleByTagEventListener(AppEvent.JournalChanged::class, mockListener2)
        eventDispatcher.sendEvent(event)
        testScheduler.advanceUntilIdle()

        verify(mockListener1, never()).onEvent(event)
        verify(mockListener2, times(1)).onEvent(event)
    }

    @Test
    fun addSingleByTagEventListener_twoListenersWithDifferentTags_eventReceivedByBoth() = runTest {
        val eventDispatcher = EventDispatcher(createTestCoroutineProvider())
        val mockListener1 = mock(EventDispatcher.EventListener::class.java)
        `when`(mockListener1.getTag()).thenReturn("testTag1")
        val mockListener2 = mock(EventDispatcher.EventListener::class.java)
        `when`(mockListener2.getTag()).thenReturn("testTag2")
        val event = AppEvent.JournalChanged

        eventDispatcher.addSingleByTagEventListener(AppEvent.JournalChanged::class, mockListener1)
        eventDispatcher.addSingleByTagEventListener(AppEvent.JournalChanged::class, mockListener2)
        eventDispatcher.sendEvent(event)
        testScheduler.advanceUntilIdle()

        verify(mockListener1, times(1)).onEvent(event)
        verify(mockListener2, times(1)).onEvent(event)
    }

    @Test
    fun removeEventListener_existed_removed() = runTest {
        val eventDispatcher = EventDispatcher(createTestCoroutineProvider())
        val mockListener = mock(EventDispatcher.EventListener::class.java)
        val event = AppEvent.JournalChanged

        eventDispatcher.addEventListener(AppEvent.JournalChanged::class, mockListener)
        eventDispatcher.removeEventListener(mockListener)
        eventDispatcher.sendEvent(event)
        testScheduler.advanceUntilIdle()

        verify(mockListener, never()).onEvent(event)
    }

    @Test
    fun removeEventListener_notExisted_noError() = runTest {
        val eventDispatcher = EventDispatcher(createTestCoroutineProvider())
        val mockListener = mock(EventDispatcher.EventListener::class.java)
        val event = AppEvent.JournalChanged

        eventDispatcher.removeEventListener(mockListener)
        eventDispatcher.sendEvent(event)
        testScheduler.advanceUntilIdle()

        verify(mockListener, never()).onEvent(event)
    }

    private fun CoroutineScope.createTestCoroutineProvider() = object : CoroutineProvider {
        override val scopeIo = this@createTestCoroutineProvider
        override val scopeMain = this@createTestCoroutineProvider
        override val scopeMainImmediate = this@createTestCoroutineProvider
        override val scopeUnconfined = this@createTestCoroutineProvider
    }
}