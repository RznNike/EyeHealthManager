package ru.rznnike.eyehealthmanager.app.dispatcher.external

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.rznnike.eyehealthmanager.domain.global.CoroutineScopeProvider
import ru.rznnike.eyehealthmanager.domain.model.common.ExternalIntentData

class ExternalIntentDispatcher(
    private val coroutineScopeProvider: CoroutineScopeProvider
) {
    private val eventsFlow = MutableStateFlow<ExternalIntentData>(
        ExternalIntentData.App().apply { processed = true }
    )

    fun subscribe() = eventsFlow.asStateFlow()

    fun send(data: ExternalIntentData) {
        coroutineScopeProvider.io.launch {
            eventsFlow.emit(data)
        }
    }
}