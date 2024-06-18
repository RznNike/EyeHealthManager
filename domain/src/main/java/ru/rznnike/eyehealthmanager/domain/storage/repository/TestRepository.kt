package ru.rznnike.eyehealthmanager.domain.storage.repository

import ru.rznnike.eyehealthmanager.domain.model.test.TestResult
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultPagingParameters

interface TestRepository {
    suspend fun getList(parameters: TestResultPagingParameters): List<TestResult>

    suspend fun getListDistinctByType(): List<TestResult>

    suspend fun add(items: List<TestResult>)

    suspend fun add(item: TestResult): Long

    suspend fun delete(id: Long)

    suspend fun deleteAll()

    suspend fun deleteDuplicates()
}