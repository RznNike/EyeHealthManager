package ru.rznnike.eyehealthmanager.data.gateway

import ru.rznnike.eyehealthmanager.data.storage.repository.TestRepository
import ru.rznnike.eyehealthmanager.domain.gateway.TestGateway
import ru.rznnike.eyehealthmanager.domain.model.TestResult
import ru.rznnike.eyehealthmanager.domain.model.TestResultPagingParams

class TestGatewayImpl(
    private val testRepository: TestRepository
) : TestGateway {
    override suspend fun getTestResults(params: TestResultPagingParams) =
        testRepository.getTests(params)

    override suspend fun addTestResults(items: List<TestResult>) =
        testRepository.addTests(items)

    override suspend fun addTestResult(item: TestResult) =
        testRepository.addTest(item)

    override suspend fun deleteTestResultById(id: Long) =
        testRepository.deleteTestById(id)

    override suspend fun deleteAllTestResults() =
        testRepository.deleteAllTests()

    override suspend fun deleteDuplicates() =
        testRepository.deleteDuplicates()
}
