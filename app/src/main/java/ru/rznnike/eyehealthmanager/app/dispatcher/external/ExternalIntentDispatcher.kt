package ru.rznnike.eyehealthmanager.app.dispatcher.external

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.rznnike.eyehealthmanager.domain.global.CoroutineProvider
import ru.rznnike.eyehealthmanager.domain.model.ExternalIntentData

class ExternalIntentDispatcher(
    private val coroutineProvider: CoroutineProvider
) {
    private val eventsFlow = MutableStateFlow<ExternalIntentData>(
        ExternalIntentData.App().apply { processed = true }
    )

    fun subscribe() = eventsFlow.asStateFlow()

    fun send(data: ExternalIntentData) {
        coroutineProvider.scopeIo.launch {
            eventsFlow.emit(data)
        }
    }
}