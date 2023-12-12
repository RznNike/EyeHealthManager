package ru.rznnike.eyehealthmanager.app.dispatcher.event

import android.net.Uri

sealed class AppEvent {
    data class JournalExported(
        val uri: Uri
    ) : AppEvent()

    data class JournalImported(
        val uri: Uri
    ) : AppEvent()

    data object JournalDuplicatesDeletionRequested : AppEvent()

    data object JournalTotalDeletionRequested : AppEvent()

    data object JournalChanged : AppEvent()

    data object TestingSettingsChanged : AppEvent()
}
