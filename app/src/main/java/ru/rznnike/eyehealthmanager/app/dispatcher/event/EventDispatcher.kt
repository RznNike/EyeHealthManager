package ru.rznnike.eyehealthmanager.app.dispatcher.event

import kotlinx.coroutines.launch
import ru.rznnike.eyehealthmanager.domain.global.CoroutineScopeProvider
import java.util.*
import kotlin.reflect.KClass

class EventDispatcher(
    private val coroutineScopeProvider: CoroutineScopeProvider
) {
    private val eventListeners = HashMap<String, MutableList<EventListener>>()

    fun addEventListener(appEventClass: KClass<out AppEvent>, listener: EventListener): EventListener {
        val key = appEventClass.java.name
        if (eventListeners[key] == null) {
            eventListeners[key] = ArrayList()
        }

        val list = eventListeners[key]
        list?.add(listener)

        return listener
    }

    fun addSingleByTagEventListener(appEventClass: KClass<out AppEvent>, listener: EventListener): EventListener {
        val key = appEventClass.java.name
        if (eventListeners[key] == null) {
            eventListeners[key] = ArrayList()
        }

        val list = eventListeners[key]
        list?.removeAll { it.getTag() == listener.getTag() }
        list?.add(listener)

        return listener
    }

    fun removeEventListener(listener: EventListener) = eventListeners
        .filter { it.value.size > 0 }
        .forEach { it.value.remove(listener) }

    fun sendEvent(appEvent: AppEvent) {
        val key = appEvent::class.java.name
        eventListeners
            .filter { it.key == key && it.value.size > 0 }
            .forEach {
                it.value.forEach { listener ->
                    coroutineScopeProvider.ui.launch {
                        listener.onEvent(appEvent)
                    }
                }
            }
    }

    interface EventListener {
        fun onEvent(event: AppEvent)

        fun getTag(): String = this::class.java.name
    }
}