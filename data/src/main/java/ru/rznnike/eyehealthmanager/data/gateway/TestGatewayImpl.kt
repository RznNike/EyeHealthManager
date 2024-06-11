package ru.rznnike.eyehealthmanager.data.gateway

import android.annotation.SuppressLint
import android.net.Uri
import kotlinx.coroutines.withContext
import ru.rznnike.eyehealthmanager.data.utils.DataConstants
import ru.rznnike.eyehealthmanager.domain.gateway.TestGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultFilter
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultPagingParameters
import ru.rznnike.eyehealthmanager.domain.model.test.TestResult
import ru.rznnike.eyehealthmanager.domain.storage.repository.TestRepository
import ru.rznnike.eyehealthmanager.domain.utils.JournalExportManager
import java.time.Clock

class TestGatewayImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val testRepository: TestRepository,
    private val clock: Clock
) : TestGateway {
    override suspend fun getTestResults(parameters: TestResultPagingParameters): List<TestResult> = withContext(dispatcherProvider.io) {
        testRepository.getList(parameters)
    }

    override suspend fun addTestResult(item: TestResult): Long = withContext(dispatcherProvider.io) {
        testRepository.add(item)
    }

    override suspend fun deleteTestResultById(id: Long) = withContext(dispatcherProvider.io) {
        testRepository.delete(id)
    }

    override suspend fun deleteAllTestResults() = withContext(dispatcherProvider.io) {
        testRepository.deleteAll()
    }

    override suspend fun deleteDuplicates() = withContext(dispatcherProvider.io) {
        testRepository.deleteDuplicates()
    }

    @SuppressLint("Recycle")
    override suspend fun exportJournal(filter: TestResultFilter, manager: JournalExportManager): Uri? = withContext(dispatcherProvider.io) {
        manager.exportJournal(
            filter = filter,
            clock = clock
        ) { callbackFilter: TestResultFilter, pageOffset: Int ->
            testRepository.getList(
                TestResultPagingParameters(
                    limit = DataConstants.EXPORT_PAGE_SIZE,
                    offset = pageOffset,
                    filter = callbackFilter
                )
            )
        }
    }

    @SuppressLint("Recycle")
    override suspend fun importJournal(importFolderUri: Uri, manager: JournalExportManager) = withContext(dispatcherProvider.io) {
        manager.importJournal(importFolderUri) { results ->
            testRepository.add(results)
        }
    }
}
