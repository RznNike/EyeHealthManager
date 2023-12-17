package ru.rznnike.eyehealthmanager.app.dispatcher.notifier

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.rznnike.eyehealthmanager.domain.global.CoroutineProvider

class Notifier(
    private val coroutineProvider: CoroutineProvider
) {
    private val notifierFlow = MutableSharedFlow<SystemMessage>()

    fun subscribe() = notifierFlow.asSharedFlow()

    fun sendMessage(
        text: String,
        level: SystemMessage.Level = SystemMessage.Level.NORMAL,
        showOnTop: Boolean = false
    ) {
        val msg = SystemMessage(
            text = text,
            type = SystemMessage.Type.BAR,
            showOnTop = showOnTop,
            level = level
        )
        coroutineProvider.scopeIo.launch {
            notifierFlow.emit(msg)
        }
    }

    fun sendMessage(
        @StringRes textRes: Int,
        level: SystemMessage.Level = SystemMessage.Level.NORMAL,
        showOnTop: Boolean = false
    ) {
        val msg = SystemMessage(
            textRes = textRes,
            type = SystemMessage.Type.BAR,
            showOnTop = showOnTop,
            level = level
        )
        coroutineProvider.scopeIo.launch {
            notifierFlow.emit(msg)
        }
    }

    fun sendAlert(text: String) {
        val msg = SystemMessage(
            text = text,
            type = SystemMessage.Type.ALERT,
            showOnTop = false
        )
        coroutineProvider.scopeIo.launch {
            notifierFlow.emit(msg)
        }
    }

    fun sendAlert(@StringRes textRes: Int) {
        val msg = SystemMessage(
            textRes = textRes,
            type = SystemMessage.Type.ALERT,
            showOnTop = false
        )
        coroutineProvider.scopeIo.launch {
            notifierFlow.emit(msg)
        }
    }

    fun sendActionMessage(
        @StringRes textRes: Int,
        @StringRes actionTextRes: Int,
        showOnTop: Boolean = false,
        actionCallback: () -> Unit
    ) {
        val msg = SystemMessage(
            textRes = textRes,
            actionTextRes = actionTextRes,
            actionCallback = actionCallback,
            type = SystemMessage.Type.BAR,
            showOnTop = showOnTop
        )
        coroutineProvider.scopeIo.launch {
            notifierFlow.emit(msg)
        }
    }

    fun sendActionMessage(
        text: String,
        actionText: String,
        showOnTop: Boolean = false,
        actionCallback: () -> Unit
    ) {
        val msg = SystemMessage(
            text = text,
            actionText = actionText,
            actionCallback = actionCallback,
            type = SystemMessage.Type.BAR,
            showOnTop = showOnTop
        )
        coroutineProvider.scopeIo.launch {
            notifierFlow.emit(msg)
        }
    }
}
