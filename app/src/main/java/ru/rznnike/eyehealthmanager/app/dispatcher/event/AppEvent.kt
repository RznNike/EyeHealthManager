package ru.rznnike.eyehealthmanager.app.dispatcher.event

import android.net.Uri

sealed class AppEvent {
    class JournalExported(
        val uri: Uri
    ) : AppEvent()

    class JournalImported(
        val uri: Uri
    ) : AppEvent()

    object JournalDuplicatesDeletionRequested : AppEvent()

    object JournalTotalDeletionRequested : AppEvent()

    object JournalChanged : AppEvent()
}
