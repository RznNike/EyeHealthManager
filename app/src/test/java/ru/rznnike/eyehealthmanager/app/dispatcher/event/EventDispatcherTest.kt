package ru.rznnike.eyehealthmanager.app.dispatcher.event

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import ru.rznnike.eyehealthmanager.app.utils.createTestCoroutineProvider

class EventDispatcherTest {
    @Test
    fun addEventListener_oneListenerAndRightEvent_eventReceived() = runTest {
        val eventDispatcher = EventDispatcher(createTestCoroutineProvider())
        val mockListener = mock<EventDispatcher.EventListener>()
        val event = AppEvent.JournalChanged

        eventDispatcher.addEventListener(AppEvent.JournalChanged::class, mockListener)
        eventDispatcher.sendEvent(event)
        testScheduler.advanceUntilIdle()

        verify(mockListener).onEvent(event)
    }

    @Test
    fun addEventListener_oneListenerAndWrongEvent_eventNotReceived() = runTest {
        val eventDispatcher = EventDispatcher(createTestCoroutineProvider())
        val mockListener = mock<EventDispatcher.EventListener>()
        val event = AppEvent.JournalDuplicatesDeletionRequested

        eventDispatcher.addEventListener(AppEvent.JournalChanged::class, mockListener)
        eventDispatcher.sendEvent(event)
        testScheduler.advanceUntilIdle()

        verifyNoInteractions(mockListener)
    }

    @Test
    fun addEventListener_twoSimilarListeners_eventReceivedByBoth() = runTest {
        val eventDispatcher = EventDispatcher(createTestCoroutineProvider())
        val mockListener1 = mock<EventDispatcher.EventListener>()
        val mockListener2 = mock<EventDispatcher.EventListener>()
        val event = AppEvent.JournalChanged

        eventDispatcher.addEventListener(AppEvent.JournalChanged::class, mockListener1)
        eventDispatcher.addEventListener(AppEvent.JournalChanged::class, mockListener2)
        eventDispatcher.sendEvent(event)
        testScheduler.advanceUntilIdle()

        verify(mockListener1).onEvent(event)
        verify(mockListener2).onEvent(event)
    }

    @Test
    fun addEventListener_twoDifferentListeners_eventReceivedByOne() = runTest {
        val eventDispatcher = EventDispatcher(createTestCoroutineProvider())
        val mockListener1 = mock<EventDispatcher.EventListener>()
        val mockListener2 = mock<EventDispatcher.EventListener>()
        val event = AppEvent.JournalChanged

        eventDispatcher.addEventListener(AppEvent.JournalChanged::class, mockListener1)
        eventDispatcher.addEventListener(AppEvent.JournalDuplicatesDeletionRequested::class, mockListener2)
        eventDispatcher.sendEvent(event)
        testScheduler.advanceUntilIdle()

        verify(mockListener1).onEvent(event)
        verifyNoInteractions(mockListener2)
    }

    @Test
    fun addSingleByTagEventListener_twoListenersWithSameTag_eventReceivedByLast() = runTest {
        val eventDispatcher = EventDispatcher(createTestCoroutineProvider())
        val mockListener1 = mock<EventDispatcher.EventListener> {
            on { getTag() } doReturn "testTag"
        }
        val mockListener2 = mock<EventDispatcher.EventListener> {
            on { getTag() } doReturn "testTag"
        }
        val event = AppEvent.JournalChanged

        eventDispatcher.addSingleByTagEventListener(AppEvent.JournalChanged::class, mockListener1)
        eventDispatcher.addSingleByTagEventListener(AppEvent.JournalChanged::class, mockListener2)
        eventDispatcher.sendEvent(event)
        testScheduler.advanceUntilIdle()

        verify(mockListener1, never()).onEvent(event)
        verify(mockListener2).onEvent(event)
    }

    @Test
    fun addSingleByTagEventListener_twoListenersWithDifferentTags_eventReceivedByBoth() = runTest {
        val eventDispatcher = EventDispatcher(createTestCoroutineProvider())
        val mockListener1 = mock<EventDispatcher.EventListener> {
            on { getTag() } doReturn "testTag1"
        }
        val mockListener2 = mock<EventDispatcher.EventListener> {
            on { getTag() } doReturn "testTag2"
        }
        val event = AppEvent.JournalChanged

        eventDispatcher.addSingleByTagEventListener(AppEvent.JournalChanged::class, mockListener1)
        eventDispatcher.addSingleByTagEventListener(AppEvent.JournalChanged::class, mockListener2)
        eventDispatcher.sendEvent(event)
        testScheduler.advanceUntilIdle()

        verify(mockListener1).onEvent(event)
        verify(mockListener2).onEvent(event)
    }

    @Test
    fun removeEventListener_existed_removed() = runTest {
        val eventDispatcher = EventDispatcher(createTestCoroutineProvider())
        val mockListener = mock<EventDispatcher.EventListener>()
        val event = AppEvent.JournalChanged

        eventDispatcher.addEventListener(AppEvent.JournalChanged::class, mockListener)
        eventDispatcher.removeEventListener(mockListener)
        eventDispatcher.sendEvent(event)
        testScheduler.advanceUntilIdle()

        verifyNoInteractions(mockListener)
    }

    @Test
    fun removeEventListener_notExisted_noError() = runTest {
        val eventDispatcher = EventDispatcher(createTestCoroutineProvider())
        val mockListener = mock<EventDispatcher.EventListener>()
        val event = AppEvent.JournalChanged

        eventDispatcher.removeEventListener(mockListener)
        eventDispatcher.sendEvent(event)
        testScheduler.advanceUntilIdle()

        verifyNoInteractions(mockListener)
    }
}