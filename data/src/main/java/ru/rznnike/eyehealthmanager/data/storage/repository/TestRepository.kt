package ru.rznnike.eyehealthmanager.data.storage.repository

import ru.rznnike.eyehealthmanager.domain.model.TestResult
import ru.rznnike.eyehealthmanager.domain.model.TestResultPagingParams

interface TestRepository {
    suspend fun getTests(params: TestResultPagingParams): List<TestResult>

    suspend fun getAllLastTests(): List<TestResult>

    suspend fun addTests(items: List<TestResult>)

    suspend fun addTest(item: TestResult): Long

    suspend fun deleteTestById(id: Long)

    suspend fun deleteAllTests()

    suspend fun deleteDuplicates()
}