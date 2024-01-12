package ru.rznnike.eyehealthmanager.data.gateway

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.rznnike.eyehealthmanager.domain.model.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.TestResultPagingParameters
import ru.rznnike.eyehealthmanager.domain.model.enums.DataGenerationType
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import java.time.Clock
import java.util.TimeZone
import kotlin.math.abs

class DevGatewayImplTest {
    @BeforeEach
    fun beforeEach() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
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
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )

        gateway.generateData(DataGenerationType.GOOD_VISION)
        val tests = fakeTestRepository.getList(parameters)
        val filteredTests = tests.filterIsInstance<AcuityTestResult>()
        val delta = (filteredTests.last().resultLeftEye ?: 0) - (filteredTests.first().resultLeftEye ?: 0)

        assertEquals(GlobalConstants.ANALYSIS_MAX_RANGE_DAYS, tests.size)
        assertEquals(GlobalConstants.ANALYSIS_MAX_RANGE_DAYS, filteredTests.size)
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
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )

        gateway.generateData(DataGenerationType.AVERAGE_VISION)
        val tests = fakeTestRepository.getList(parameters)
        val filteredTests = tests.filterIsInstance<AcuityTestResult>()
        val delta = (filteredTests.last().resultLeftEye ?: 0) - (filteredTests.first().resultLeftEye ?: 0)

        assertEquals(GlobalConstants.ANALYSIS_MAX_RANGE_DAYS, tests.size)
        assertEquals(GlobalConstants.ANALYSIS_MAX_RANGE_DAYS, filteredTests.size)
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
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )

        gateway.generateData(DataGenerationType.BAD_VISION)
        val tests = fakeTestRepository.getList(parameters)
        val filteredTests = tests.filterIsInstance<AcuityTestResult>()
        val delta = (filteredTests.last().resultLeftEye ?: 0) - (filteredTests.first().resultLeftEye ?: 0)

        assertEquals(GlobalConstants.ANALYSIS_MAX_RANGE_DAYS, tests.size)
        assertEquals(GlobalConstants.ANALYSIS_MAX_RANGE_DAYS, filteredTests.size)
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