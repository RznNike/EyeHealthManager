package ru.rznnike.eyehealthmanager.domain.utils

import android.net.Uri
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultFilter
import ru.rznnike.eyehealthmanager.domain.model.test.TestResult
import java.time.Clock

interface JournalBackupManager {
    suspend fun exportJournal(
        filter: TestResultFilter,
        clock: Clock,
        readDataFromDBCallback: suspend (filter: TestResultFilter, pageOffset: Int) -> List<TestResult>
    ): Uri?

    suspend fun importJournal(
        importFolderUri: Uri,
        writeDataToDBCallback: suspend (List<TestResult>) -> Unit
    )
}