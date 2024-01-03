package ru.rznnike.eyehealthmanager.data.storage.repository

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.rznnike.eyehealthmanager.data.gateway.DevGatewayImpl
import ru.rznnike.eyehealthmanager.domain.model.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.ContrastTestResult
import ru.rznnike.eyehealthmanager.domain.model.TestResult
import ru.rznnike.eyehealthmanager.domain.model.TestResultFilter
import ru.rznnike.eyehealthmanager.domain.model.TestResultPagingParameters
import ru.rznnike.eyehealthmanager.domain.model.enums.DataGenerationType
import ru.rznnike.eyehealthmanager.domain.model.enums.TestType
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants

class TestRepositoryImplTest : AbstractObjectBoxTest() {
    @Test
    fun getTests_empty_success() = runTest {
        val repository = TestRepositoryImpl(store!!)
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = System.currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        val tests = repository.getTests(parameters)

        assert(tests.isEmpty())
    }

    @Test
    fun getTests_withData_success() = runTest {
        val repository = TestRepositoryImpl(store!!)
        val generator = DevGatewayImpl(repository)
        generator.generateData(DataGenerationType.GOOD_VISION)
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = System.currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        val tests = repository.getTests(parameters)

        assertEquals(GlobalConstants.ANALYSIS_MAX_RANGE_DAYS, tests.size)
    }

    @Test
    fun getTests_disorderedData_returnOrderedByTimestampDesc() = runTest {
        val repository = TestRepositoryImpl(store!!)
        repository.addTest(
            AcuityTestResult(
                timestamp = 10_000
            )
        )
        repository.addTest(
            AcuityTestResult(
                timestamp = 30_000
            )
        )
        repository.addTest(
            AcuityTestResult(
                timestamp = 20_000
            )
        )
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = System.currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        val tests = repository.getTests(parameters)

        assertEquals(3, tests.size)
        assert(tests[0].timestamp > tests[1].timestamp)
        assert(tests[1].timestamp > tests[2].timestamp)
    }

    @Test
    fun getTests_filterByDate_success() = runTest {
        val repository = TestRepositoryImpl(store!!)
        val generator = DevGatewayImpl(repository)
        generator.generateData(DataGenerationType.GOOD_VISION)
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = true,
                filterByType = false,
                dateFrom = System.currentTimeMillis() - 10 * GlobalConstants.DAY_MS,
                dateTo = System.currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        val tests = repository.getTests(parameters)

        assert(tests.isNotEmpty())
        assert(tests.minOf { it.timestamp } >= (parameters.filter?.dateFrom ?: 0))
    }

    @Test
    fun getTests_filterByType_success() = runTest {
        val repository = TestRepositoryImpl(store!!)
        val generator = DevGatewayImpl(repository)
        generator.generateData(DataGenerationType.GOOD_VISION)
        generator.generateData(DataGenerationType.OTHER_TESTS)
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = true,
                dateFrom = 0,
                dateTo = System.currentTimeMillis(),
                selectedTestTypes = mutableListOf(TestType.CONTRAST)
            )
        )

        val tests = repository.getTests(parameters)

        assertEquals(1, tests.size)
        assert(tests.first() is ContrastTestResult)
    }

    @Test
    fun getTests_allFilters_success() = runTest {
        val repository = TestRepositoryImpl(store!!)
        val generator = DevGatewayImpl(repository)
        generator.generateData(DataGenerationType.GOOD_VISION)
        generator.generateData(DataGenerationType.OTHER_TESTS)
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = true,
                filterByType = true,
                dateFrom = System.currentTimeMillis() - 10 * GlobalConstants.DAY_MS,
                dateTo = System.currentTimeMillis(),
                selectedTestTypes = mutableListOf(TestType.ACUITY)
            )
        )

        val tests = repository.getTests(parameters)

        assert(tests.isNotEmpty())
        assert(tests.minOf { it.timestamp } >= (parameters.filter?.dateFrom ?: 0))
        assert(tests.all { it is AcuityTestResult })
    }

    @Test
    fun getAllLastTests_empty_success() = runTest {
        val repository = TestRepositoryImpl(store!!)

        val tests = repository.getAllLastTests()

        assert(tests.isEmpty())
    }

    @Test
    fun getAllLastTests_acuityOnly_success() = runTest {
        val repository = TestRepositoryImpl(store!!)
        val generator = DevGatewayImpl(repository)
        generator.generateData(DataGenerationType.GOOD_VISION)

        val tests = repository.getAllLastTests()

        assertEquals(1, tests.size)
        assert(tests.first() is AcuityTestResult)
    }

    @Test
    fun getAllLastTests_allTests_success() = runTest {
        val repository = TestRepositoryImpl(store!!)
        val generator = DevGatewayImpl(repository)
        generator.generateData(DataGenerationType.GOOD_VISION)
        generator.generateData(DataGenerationType.OTHER_TESTS)

        val tests = repository.getAllLastTests()
        val testsDistinctByType = tests.distinctBy { it.javaClass }

        assertEquals(TestType.entries.size, tests.size)
        assertEquals(tests.size, testsDistinctByType.size)
    }

    @Test
    fun addTests_empty_success() = runTest {
        val repository = TestRepositoryImpl(store!!)
        val tests = emptyList<TestResult>()
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = System.currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        repository.addTests(tests)
        val resultTests = repository.getTests(parameters)

        assert(resultTests.isEmpty())
    }

    @Test
    fun addTests_withData_success() = runTest {
        val repository = TestRepositoryImpl(store!!)
        val tests = listOf(
            AcuityTestResult(
                timestamp = 10_000
            ),
            AcuityTestResult(
                timestamp = 20_000
            ),
            AcuityTestResult(
                timestamp = 30_000
            )
        )
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = System.currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        repository.addTests(tests)
        val resultTests = repository.getTests(parameters)

        assertEquals(tests.size, resultTests.size)
    }

    @Test
    fun addTest_success() = runTest {
        val repository = TestRepositoryImpl(store!!)
        val test = AcuityTestResult(
            timestamp = 10_000
        )
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = System.currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        val id = repository.addTest(test)
        val resultTests = repository.getTests(parameters)

        assertEquals(1, resultTests.size)
        assertEquals(id, resultTests.first().id)
        assertEquals(test.timestamp, resultTests.first().timestamp)
    }

    @Test
    fun deleteTestById_correctId_success() = runTest {
        val repository = TestRepositoryImpl(store!!)
        val test1 = AcuityTestResult(
            timestamp = 10_000
        )
        val test2 = AcuityTestResult(
            timestamp = 20_000
        )
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = System.currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )
        val id1 = repository.addTest(test1)
        val id2 = repository.addTest(test2)

        repository.deleteTestById(id1)
        val resultTests = repository.getTests(parameters)

        assertEquals(1, resultTests.size)
        assertEquals(id2, resultTests.first().id)
    }

    @Test
    fun deleteTestById_badId_success() = runTest {
        val repository = TestRepositoryImpl(store!!)
        val test1 = AcuityTestResult(
            timestamp = 10_000
        )
        val test2 = AcuityTestResult(
            timestamp = 20_000
        )
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = System.currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )
        val id1 = repository.addTest(test1)
        val id2 = repository.addTest(test2)

        repository.deleteTestById(id1 + id2 + 1)
        val resultTests = repository.getTests(parameters)

        assertEquals(2, resultTests.size)
    }

    @Test
    fun deleteAllTests_empty_success() = runTest {
        val repository = TestRepositoryImpl(store!!)
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = System.currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        repository.deleteAllTests()
        val resultTests = repository.getTests(parameters)

        assert(resultTests.isEmpty())
    }

    @Test
    fun deleteAllTests_withData_success() = runTest {
        val repository = TestRepositoryImpl(store!!)
        val generator = DevGatewayImpl(repository)
        generator.generateData(DataGenerationType.GOOD_VISION)
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = System.currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        val oldTests = repository.getTests(parameters)
        repository.deleteAllTests()
        val resultTests = repository.getTests(parameters)

        assert(oldTests.isNotEmpty())
        assert(resultTests.isEmpty())
    }

    @Test
    fun deleteDuplicates_noDuplicates_noChanges() = runTest {
        val repository = TestRepositoryImpl(store!!)
        val tests = listOf(
            AcuityTestResult(
                timestamp = 10_000
            ),
            AcuityTestResult(
                timestamp = 20_000
            ),
            AcuityTestResult(
                timestamp = 30_000
            )
        )
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = System.currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )
        repository.addTests(tests)

        val oldTests = repository.getTests(parameters)
        repository.deleteDuplicates()
        val resultTests = repository.getTests(parameters)

        assert(oldTests.isNotEmpty())
        assertEquals(tests.size, oldTests.size)
        assertEquals(oldTests.size, resultTests.size)
    }

    @Test
    fun deleteDuplicates_withDuplicates_success() = runTest {
        val repository = TestRepositoryImpl(store!!)
        val tests = listOf(
            AcuityTestResult(
                timestamp = 10_000
            ),
            AcuityTestResult(
                timestamp = 20_000
            ),
            AcuityTestResult(
                timestamp = 30_000
            )
        )
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = System.currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )
        repository.addTests(tests)
        repository.addTests(tests)

        val oldTests = repository.getTests(parameters)
        repository.deleteDuplicates()
        val resultTests = repository.getTests(parameters)

        assert(oldTests.isNotEmpty())
        assertEquals(tests.size * 2, oldTests.size)
        assertEquals(tests.size, resultTests.size)
    }
}