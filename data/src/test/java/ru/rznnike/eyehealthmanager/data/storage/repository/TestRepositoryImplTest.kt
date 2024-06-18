package ru.rznnike.eyehealthmanager.data.storage.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.rznnike.eyehealthmanager.data.gateway.DevGatewayImpl
import ru.rznnike.eyehealthmanager.data.storage.AbstractObjectBoxTest
import ru.rznnike.eyehealthmanager.data.storage.dao.AcuityTestDAO
import ru.rznnike.eyehealthmanager.data.storage.dao.AstigmatismTestDAO
import ru.rznnike.eyehealthmanager.data.storage.dao.ColorPerceptionTestDAO
import ru.rznnike.eyehealthmanager.data.storage.dao.ContrastTestDAO
import ru.rznnike.eyehealthmanager.data.storage.dao.DaltonismTestDAO
import ru.rznnike.eyehealthmanager.data.storage.dao.NearFarTestDAO
import ru.rznnike.eyehealthmanager.data.storage.dao.TestDAO
import ru.rznnike.eyehealthmanager.data.utils.DataConstants
import ru.rznnike.eyehealthmanager.data.utils.createTestDispatcherProvider
import ru.rznnike.eyehealthmanager.domain.model.common.DataGenerationType
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultFilter
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultPagingParameters
import ru.rznnike.eyehealthmanager.domain.model.test.TestResult
import ru.rznnike.eyehealthmanager.domain.model.test.TestType
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.contrast.ContrastTestResult
import ru.rznnike.eyehealthmanager.domain.utils.currentTimeMillis
import java.time.Clock
import java.util.TimeZone

class TestRepositoryImplTest : AbstractObjectBoxTest() {
    private val testDispatcher = StandardTestDispatcher()
    private val testDispatcherProvider = testDispatcher.createTestDispatcherProvider()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    override fun beforeEach() {
        super.beforeEach()
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    override fun afterEach() {
        super.afterEach()
        Dispatchers.resetMain()
    }

    @Test
    fun getList_empty_success() = runTest {
        val repository = createRepository()
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        val tests = repository.getList(parameters)

        assertTrue(tests.isEmpty())
    }

    @Test
    fun getList_withData_success() = runTest {
        val repository = createRepository()
        val generator = DevGatewayImpl(
            dispatcherProvider = testDispatcherProvider,
            testRepository = repository,
            clock = Clock.systemUTC()
        )
        generator.generateData(DataGenerationType.GOOD_VISION)
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        val tests = repository.getList(parameters)

        assertEquals(DataConstants.ANALYSIS_MAX_RANGE_DAYS, tests.size.toLong())
    }

    @Test
    fun getList_disorderedData_returnOrderedByTimestampDesc() = runTest {
        val repository = createRepository()
        repository.add(
            AcuityTestResult(
                timestamp = 10_000
            )
        )
        repository.add(
            AcuityTestResult(
                timestamp = 30_000
            )
        )
        repository.add(
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
                dateTo = currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        val tests = repository.getList(parameters)

        assertEquals(3, tests.size)
        assertTrue(tests[0].timestamp > tests[1].timestamp)
        assertTrue(tests[1].timestamp > tests[2].timestamp)
    }

    @Test
    fun getList_filterByDate_success() = runTest {
        val repository = createRepository()
        val generator = DevGatewayImpl(
            dispatcherProvider = testDispatcherProvider,
            testRepository = repository,
            clock = Clock.systemUTC()
        )
        generator.generateData(DataGenerationType.GOOD_VISION)
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = true,
                filterByType = false,
                dateFrom = currentTimeMillis() - 10 * DataConstants.DAY_MS,
                dateTo = currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        val tests = repository.getList(parameters)

        assertTrue(tests.isNotEmpty())
        assertTrue(tests.minOf { it.timestamp } >= (parameters.filter?.dateFrom ?: 0))
    }

    @Test
    fun getList_filterByType_success() = runTest {
        val repository = createRepository()
        val generator = DevGatewayImpl(
            dispatcherProvider = testDispatcherProvider,
            testRepository = repository,
            clock = Clock.systemUTC()
        )
        generator.generateData(DataGenerationType.GOOD_VISION)
        generator.generateData(DataGenerationType.OTHER_TESTS)
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = true,
                dateFrom = 0,
                dateTo = currentTimeMillis(),
                selectedTestTypes = mutableListOf(TestType.CONTRAST)
            )
        )

        val tests = repository.getList(parameters)

        assertEquals(1, tests.size)
        assertTrue(tests.first() is ContrastTestResult)
    }

    @Test
    fun getList_allFilters_success() = runTest {
        val repository = createRepository()
        val generator = DevGatewayImpl(
            dispatcherProvider = testDispatcherProvider,
            testRepository = repository,
            clock = Clock.systemUTC()
        )
        generator.generateData(DataGenerationType.GOOD_VISION)
        generator.generateData(DataGenerationType.OTHER_TESTS)
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = true,
                filterByType = true,
                dateFrom = currentTimeMillis() - 10 * DataConstants.DAY_MS,
                dateTo = currentTimeMillis(),
                selectedTestTypes = mutableListOf(TestType.ACUITY)
            )
        )

        val tests = repository.getList(parameters)

        assertTrue(tests.isNotEmpty())
        assertTrue(tests.minOf { it.timestamp } >= (parameters.filter?.dateFrom ?: 0))
        assertTrue(tests.all { it is AcuityTestResult })
    }

    @Test
    fun getListDistinctByType_empty_success() = runTest {
        val repository = createRepository()

        val tests = repository.getListDistinctByType()

        assertTrue(tests.isEmpty())
    }

    @Test
    fun getListDistinctByType_acuityOnly_success() = runTest {
        val repository = createRepository()
        val generator = DevGatewayImpl(
            dispatcherProvider = testDispatcherProvider,
            testRepository = repository,
            clock = Clock.systemUTC()
        )
        generator.generateData(DataGenerationType.GOOD_VISION)

        val tests = repository.getListDistinctByType()

        assertEquals(1, tests.size)
        assertTrue(tests.first() is AcuityTestResult)
    }

    @Test
    fun getListDistinctByType_allTests_success() = runTest {
        val repository = createRepository()
        val generator = DevGatewayImpl(
            dispatcherProvider = testDispatcherProvider,
            testRepository = repository,
            clock = Clock.systemUTC()
        )
        generator.generateData(DataGenerationType.GOOD_VISION)
        generator.generateData(DataGenerationType.OTHER_TESTS)

        val tests = repository.getListDistinctByType()
        val testsDistinctByType = tests.distinctBy { it.javaClass }

        assertEquals(TestType.entries.size, tests.size)
        assertEquals(tests.size, testsDistinctByType.size)
    }

    @Test
    fun addList_empty_success() = runTest {
        val repository = createRepository()
        val tests = emptyList<TestResult>()
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        repository.add(tests)
        val resultTests = repository.getList(parameters)

        assertTrue(resultTests.isEmpty())
    }

    @Test
    fun addList_withData_success() = runTest {
        val repository = createRepository()
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
                dateTo = currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        repository.add(tests)
        val resultTests = repository.getList(parameters)

        assertEquals(tests.size, resultTests.size)
    }

    @Test
    fun add_success() = runTest {
        val repository = createRepository()
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
                dateTo = currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        val id = repository.add(test)
        val resultTests = repository.getList(parameters)

        assertEquals(1, resultTests.size)
        assertEquals(id, resultTests.first().id)
        assertEquals(test.timestamp, resultTests.first().timestamp)
    }

    @Test
    fun delete_correctId_success() = runTest {
        val repository = createRepository()
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
                dateTo = currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )
        val id1 = repository.add(test1)
        val id2 = repository.add(test2)

        repository.delete(id1)
        val resultTests = repository.getList(parameters)

        assertEquals(1, resultTests.size)
        assertEquals(id2, resultTests.first().id)
    }

    @Test
    fun delete_badId_success() = runTest {
        val repository = createRepository()
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
                dateTo = currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )
        val id1 = repository.add(test1)
        val id2 = repository.add(test2)

        repository.delete(id1 + id2 + 1)
        val resultTests = repository.getList(parameters)

        assertEquals(2, resultTests.size)
    }

    @Test
    fun deleteAll_empty_success() = runTest {
        val repository = createRepository()
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        repository.deleteAll()
        val resultTests = repository.getList(parameters)

        assertTrue(resultTests.isEmpty())
    }

    @Test
    fun deleteAll_withData_success() = runTest {
        val repository = createRepository()
        val generator = DevGatewayImpl(
            dispatcherProvider = testDispatcherProvider,
            testRepository = repository,
            clock = Clock.systemUTC()
        )
        generator.generateData(DataGenerationType.GOOD_VISION)
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        val oldTests = repository.getList(parameters)
        repository.deleteAll()
        val resultTests = repository.getList(parameters)

        assertTrue(oldTests.isNotEmpty())
        assertTrue(resultTests.isEmpty())
    }

    @Test
    fun deleteDuplicates_noDuplicates_noChanges() = runTest {
        val repository = createRepository()
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
                dateTo = currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )
        repository.add(tests)

        val oldTests = repository.getList(parameters)
        repository.deleteDuplicates()
        val resultTests = repository.getList(parameters)

        assertTrue(oldTests.isNotEmpty())
        assertEquals(tests.size, oldTests.size)
        assertEquals(oldTests.size, resultTests.size)
    }

    @Test
    fun deleteDuplicates_withDuplicates_success() = runTest {
        val repository = createRepository()
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
                dateTo = currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )
        repository.add(tests)
        repository.add(tests)

        val oldTests = repository.getList(parameters)
        repository.deleteDuplicates()
        val resultTests = repository.getList(parameters)

        assertTrue(oldTests.isNotEmpty())
        assertEquals(tests.size * 2, oldTests.size)
        assertEquals(tests.size, resultTests.size)
    }

    private fun createRepository() = TestRepositoryImpl(
        boxStore = store!!,
        testDAO = TestDAO(store!!),
        acuityTestDAO = AcuityTestDAO(store!!),
        astigmatismTestDAO = AstigmatismTestDAO(store!!),
        colorPerceptionTestDAO = ColorPerceptionTestDAO(store!!),
        contrastTestDAO = ContrastTestDAO(store!!),
        daltonismTestDAO = DaltonismTestDAO(store!!),
        nearFarTestDAO = NearFarTestDAO(store!!),
    )
}