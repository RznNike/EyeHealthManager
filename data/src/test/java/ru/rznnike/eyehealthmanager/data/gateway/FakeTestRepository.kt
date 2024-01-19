package ru.rznnike.eyehealthmanager.data.gateway

import ru.rznnike.eyehealthmanager.domain.model.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.TestResult
import ru.rznnike.eyehealthmanager.domain.model.TestResultPagingParameters
import ru.rznnike.eyehealthmanager.domain.storage.repository.TestRepository

class FakeTestRepository : TestRepository {
    val tests = mutableListOf<TestResult>()

    override suspend fun getList(parameters: TestResultPagingParameters) = tests
        .filter { testResult ->
            parameters.filter?.let { filter ->
                ((!filter.filterByDate) || LongRange(filter.dateFrom, filter.dateTo).contains(testResult.timestamp))
                        && ((!filter.filterByType) || (testResult is AcuityTestResult))
            } ?: true
        }

    override suspend fun getListDistinctByType() = tests
        .filter { it !is AcuityTestResult }
        .sortedByDescending { it.timestamp }
        .distinctBy { it.javaClass }

    override suspend fun add(items: List<TestResult>) {
        tests.addAll(items)
    }

    override suspend fun add(item: TestResult): Long {
        tests.add(item)
        return item.id
    }

    override suspend fun delete(id: Long) {
        tests.removeAll { it.id == id }
    }

    override suspend fun deleteAll() {
        tests.clear()
    }

    override suspend fun deleteDuplicates() = Unit
}