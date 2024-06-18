package ru.rznnike.eyehealthmanager.data.gateway

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
import ru.rznnike.eyehealthmanager.data.utils.DataConstants
import ru.rznnike.eyehealthmanager.data.utils.createTestDispatcherProvider
import ru.rznnike.eyehealthmanager.domain.model.common.DataGenerationType
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultPagingParameters
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestResult
import java.time.Clock
import java.util.TimeZone
import kotlin.math.abs

class DevGatewayImplTest {
    private val testDispatcher = StandardTestDispatcher()
    private val testDispatcherProvider = testDispatcher.createTestDispatcherProvider()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun beforeEach() {
        Dispatchers.setMain(testDispatcher)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun afterEach() {
        Dispatchers.resetMain()
    }

    @Test
    fun generateData_goodVision_success() = runTest {
        val fakeTestRepository = FakeTestRepository()
        val parameters = TestResultPagingParameters(
            limit = Int.MAX_VALUE,
            offset = 0,
            filter = null
        )
        val gateway = DevGatewayImpl(
            dispatcherProvider = testDispatcherProvider,
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )

        gateway.generateData(DataGenerationType.GOOD_VISION)
        val tests = fakeTestRepository.getList(parameters)
        val filteredTests = tests.filterIsInstance<AcuityTestResult>()
        val delta = (filteredTests.last().resultLeftEye ?: 0) - (filteredTests.first().resultLeftEye ?: 0)

        assertEquals(DataConstants.ANALYSIS_MAX_RANGE_DAYS, tests.size.toLong())
        assertEquals(DataConstants.ANALYSIS_MAX_RANGE_DAYS, filteredTests.size.toLong())
        assertTrue(delta > 10)
    }

    @Test
    fun generateData_averageVision_success() = runTest {
        val fakeTestRepository = FakeTestRepository()
        val parameters = TestResultPagingParameters(
            limit = Int.MAX_VALUE,
            offset = 0,
            filter = null
        )
        val gateway = DevGatewayImpl(
            dispatcherProvider = testDispatcherProvider,
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )

        gateway.generateData(DataGenerationType.AVERAGE_VISION)
        val tests = fakeTestRepository.getList(parameters)
        val filteredTests = tests.filterIsInstance<AcuityTestResult>()
        val delta = (filteredTests.last().resultLeftEye ?: 0) - (filteredTests.first().resultLeftEye ?: 0)

        assertEquals(DataConstants.ANALYSIS_MAX_RANGE_DAYS, tests.size.toLong())
        assertEquals(DataConstants.ANALYSIS_MAX_RANGE_DAYS, filteredTests.size.toLong())
        assertTrue(abs(delta) < 7)
    }

    @Test
    fun generateData_badVision_success() = runTest {
        val fakeTestRepository = FakeTestRepository()
        val parameters = TestResultPagingParameters(
            limit = Int.MAX_VALUE,
            offset = 0,
            filter = null
        )
        val gateway = DevGatewayImpl(
            dispatcherProvider = testDispatcherProvider,
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )

        gateway.generateData(DataGenerationType.BAD_VISION)
        val tests = fakeTestRepository.getList(parameters)
        val filteredTests = tests.filterIsInstance<AcuityTestResult>()
        val delta = (filteredTests.last().resultLeftEye ?: 0) - (filteredTests.first().resultLeftEye ?: 0)

        assertEquals(DataConstants.ANALYSIS_MAX_RANGE_DAYS, tests.size.toLong())
        assertEquals(DataConstants.ANALYSIS_MAX_RANGE_DAYS, filteredTests.size.toLong())
        assertTrue(delta < -10)
    }

    @Test
    fun generateData_otherTests_success() = runTest {
        val fakeTestRepository = FakeTestRepository()
        val parameters = TestResultPagingParameters(
            limit = Int.MAX_VALUE,
            offset = 0,
            filter = null
        )
        val gateway = DevGatewayImpl(
            dispatcherProvider = testDispatcherProvider,
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )

        gateway.generateData(DataGenerationType.OTHER_TESTS)
        val tests = fakeTestRepository.getList(parameters)
        val filteredTests = tests.filter { it !is AcuityTestResult }

        assertEquals(5, tests.size)
        assertEquals(5, filteredTests.size)
    }
}