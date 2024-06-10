package ru.rznnike.eyehealthmanager.app.dispatcher.notifier

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.rznnike.eyehealthmanager.domain.global.CoroutineScopeProvider

class Notifier(
    private val coroutineScopeProvider: CoroutineScopeProvider
) {
    private val notifierFlow = MutableSharedFlow<SystemMessage>()

    fun subscribe() = notifierFlow.asSharedFlow()

    fun sendMessage(
        text: String,
        level: SystemMessage.Level = SystemMessage.Level.NORMAL,
        showOnTop: Boolean = false
    ) = emitMessage(
        SystemMessage(
            text = text,
            type = SystemMessage.Type.BAR,
            showOnTop = showOnTop,
            level = level
        )
    )

    fun sendMessage(
        @StringRes textRes: Int,
        level: SystemMessage.Level = SystemMessage.Level.NORMAL,
        showOnTop: Boolean = false
    ) = emitMessage(
        SystemMessage(
            textRes = textRes,
            type = SystemMessage.Type.BAR,
            showOnTop = showOnTop,
            level = level
        )
    )

    fun sendAlert(text: String) = emitMessage(
        SystemMessage(
            text = text,
            type = SystemMessage.Type.ALERT,
            showOnTop = false
        )
    )

    fun sendAlert(@StringRes textRes: Int) = emitMessage(
        SystemMessage(
            textRes = textRes,
            type = SystemMessage.Type.ALERT,
            showOnTop = false
        )
    )

    fun sendActionMessage(
        @StringRes textRes: Int,
        @StringRes actionTextRes: Int,
        showOnTop: Boolean = false,
        actionCallback: () -> Unit
    ) = emitMessage(
        SystemMessage(
            textRes = textRes,
            actionTextRes = actionTextRes,
            actionCallback = actionCallback,
            type = SystemMessage.Type.BAR,
            showOnTop = showOnTop
        )
    )

    fun sendActionMessage(
        text: String,
        actionText: String,
        showOnTop: Boolean = false,
        actionCallback: () -> Unit
    ) = emitMessage(
        SystemMessage(
            text = text,
            actionText = actionText,
            actionCallback = actionCallback,
            type = SystemMessage.Type.BAR,
            showOnTop = showOnTop
        )
    )

    private fun emitMessage(message: SystemMessage) {
        coroutineScopeProvider.io.launch {
            notifierFlow.emit(message)
        }
    }
}
