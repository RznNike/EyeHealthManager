package ru.rznnike.eyehealthmanager.domain.gateway

import android.net.Uri
import ru.rznnike.eyehealthmanager.domain.model.TestResult
import ru.rznnike.eyehealthmanager.domain.model.TestResultFilterParams
import ru.rznnike.eyehealthmanager.domain.model.TestResultPagingParams

interface TestGateway {
    suspend fun getTestResults(params: TestResultPagingParams): List<TestResult>

    suspend fun addTestResults(items: List<TestResult>)

    suspend fun addTestResult(item: TestResult): Long

    suspend fun deleteTestResultById(id: Long)

    suspend fun deleteAllTestResults()

    suspend fun deleteDuplicates()

    suspend fun exportJournal(filterParams: TestResultFilterParams): Uri?
}
