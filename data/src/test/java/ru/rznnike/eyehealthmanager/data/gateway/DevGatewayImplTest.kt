package ru.rznnike.eyehealthmanager.data.gateway

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Test
import ru.rznnike.eyehealthmanager.domain.model.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.TestResult
import ru.rznnike.eyehealthmanager.domain.model.TestResultPagingParameters
import ru.rznnike.eyehealthmanager.domain.model.enums.DataGenerationType
import ru.rznnike.eyehealthmanager.domain.storage.repository.TestRepository
import java.util.TimeZone
import kotlin.math.abs

class DevGatewayImplTest {
    private val fakeTestRepository = object : TestRepository {
        private val tests = mutableListOf<TestResult>()

        override suspend fun getTests(parameters: TestResultPagingParameters) = tests

        override suspend fun getAllLastTests() = tests
            .filter { it !is AcuityTestResult }
            .sortedByDescending { it.timestamp }
            .distinctBy { it.javaClass }

        override suspend fun addTests(items: List<TestResult>) {
            tests.addAll(items)
        }

        override suspend fun addTest(item: TestResult): Long {
            tests.add(item)
            return item.id
        }

        override suspend fun deleteTestById(id: Long) {
            tests.removeAll { it.id == id }
        }

        override suspend fun deleteAllTests() {
            tests.clear()
        }

        override suspend fun deleteDuplicates() = Unit
    }

    @BeforeEach
    fun beforeEach() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Test
    fun generateData_goodVision_success() = runTest {
        fakeTestRepository.deleteAllTests()
        val parameters = TestResultPagingParameters(
            limit = Int.MAX_VALUE,
            offset = 0,
            filter = null
        )
        val gateway = DevGatewayImpl(fakeTestRepository)

        gateway.generateData(DataGenerationType.GOOD_VISION)
        val tests = fakeTestRepository.getTests(parameters)
        val filteredTests = tests.filterIsInstance<AcuityTestResult>()
        val delta = (filteredTests.last().resultLeftEye ?: 0) - (filteredTests.first().resultLeftEye ?: 0)

        assertEquals(90, tests.size)
        assertEquals(90, filteredTests.size)
        assert(delta > 10)
    }

    @Test
    fun generateData_averageVision_success() = runTest {
        fakeTestRepository.deleteAllTests()
        val parameters = TestResultPagingParameters(
            limit = Int.MAX_VALUE,
            offset = 0,
            filter = null
        )
        val gateway = DevGatewayImpl(fakeTestRepository)

        gateway.generateData(DataGenerationType.AVERAGE_VISION)
        val tests = fakeTestRepository.getTests(parameters)
        val filteredTests = tests.filterIsInstance<AcuityTestResult>()
        val delta = (filteredTests.last().resultLeftEye ?: 0) - (filteredTests.first().resultLeftEye ?: 0)

        assertEquals(90, tests.size)
        assertEquals(90, filteredTests.size)
        assert(abs(delta) < 7)
    }

    @Test
    fun generateData_badVision_success() = runTest {
        fakeTestRepository.deleteAllTests()
        val parameters = TestResultPagingParameters(
            limit = Int.MAX_VALUE,
            offset = 0,
            filter = null
        )
        val gateway = DevGatewayImpl(fakeTestRepository)

        gateway.generateData(DataGenerationType.BAD_VISION)
        val tests = fakeTestRepository.getTests(parameters)
        val filteredTests = tests.filterIsInstance<AcuityTestResult>()
        val delta = (filteredTests.last().resultLeftEye ?: 0) - (filteredTests.first().resultLeftEye ?: 0)

        assertEquals(90, tests.size)
        assertEquals(90, filteredTests.size)
        assert(delta < -10)
    }

    @Test
    fun generateData_otherTests_success() = runTest {
        fakeTestRepository.deleteAllTests()
        val parameters = TestResultPagingParameters(
            limit = Int.MAX_VALUE,
            offset = 0,
            filter = null
        )
        val gateway = DevGatewayImpl(fakeTestRepository)

        gateway.generateData(DataGenerationType.OTHER_TESTS)
        val tests = fakeTestRepository.getTests(parameters)
        val filteredTests = tests.filter { it !is AcuityTestResult }

        assertEquals(5, tests.size)
        assertEquals(5, filteredTests.size)
    }
}