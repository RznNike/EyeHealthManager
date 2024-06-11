package ru.rznnike.eyehealthmanager.domain.gateway

import android.net.Uri
import ru.rznnike.eyehealthmanager.domain.model.test.TestResult
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultFilter
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultPagingParameters
import ru.rznnike.eyehealthmanager.domain.model.test.TestType
import ru.rznnike.eyehealthmanager.domain.utils.JournalExportManager

interface TestGateway {
    suspend fun getTestResults(parameters: TestResultPagingParameters): List<TestResult>

    suspend fun addTestResult(item: TestResult): Long

    suspend fun deleteTestResultById(id: Long)

    suspend fun deleteAllTestResults()

    suspend fun deleteDuplicates()

    suspend fun exportJournal(filter: TestResultFilter, manager: JournalExportManager): Uri?

    suspend fun importJournal(importFolderUri: Uri, manager: JournalExportManager)
}
