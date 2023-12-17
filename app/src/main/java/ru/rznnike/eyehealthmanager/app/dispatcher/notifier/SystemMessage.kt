package ru.rznnike.eyehealthmanager.app.dispatcher.notifier

import androidx.annotation.StringRes

data class SystemMessage(
    @StringRes val textRes: Int? = null,
    var text: String? = null,
    @StringRes val actionTextRes: Int? = null,
    var actionText: String? = null,
    val actionCallback: (() -> Unit)? = null,
    val type: Type,
    val showOnTop: Boolean,
    val level: Level = Level.NORMAL
) {
    enum class Type {
        ALERT,
        BAR
    }

    enum class Level {
        NORMAL,
        ERROR
    }
}