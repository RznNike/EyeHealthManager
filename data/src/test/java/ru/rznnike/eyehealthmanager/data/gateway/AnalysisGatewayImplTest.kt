package ru.rznnike.eyehealthmanager.data.gateway

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.rznnike.eyehealthmanager.domain.model.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.AnalysisParameters
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.enums.AnalysisType
import ru.rznnike.eyehealthmanager.domain.model.enums.DataGenerationType
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType
import ru.rznnike.eyehealthmanager.domain.model.exception.NotEnoughDataException
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.currentTimeMillis
import java.time.Clock
import java.util.TimeZone

class AnalysisGatewayImplTest {
    @BeforeEach
    fun beforeEach() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Test
    fun getAnalysisResult_noData_exception() = runTest {
        val fakeTestRepository = FakeTestRepository()
        val gateway = AnalysisGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        assertThrows<NotEnoughDataException> {
            gateway.getAnalysisResult(parameters)
        }
    }

    @Test
    fun getAnalysisResult_notEnoughData_exception() = runTest {
        val fakeTestRepository = FakeTestRepository()
        fakeTestRepository.add(
            AcuityTestResult()
        )
        val gateway = AnalysisGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        assertThrows<NotEnoughDataException> {
            gateway.getAnalysisResult(parameters)
        }
    }

    @Test
    fun getAnalysisResult_positiveData_noWarning() = runTest {
        val fakeTestRepository = FakeTestRepository()
        val generator = DevGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        generator.generateData(DataGenerationType.GOOD_VISION)
        val gateway = AnalysisGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        val result = gateway.getAnalysisResult(parameters)

        assertFalse(result.showWarningAboutVision)
    }

    @Test
    fun getAnalysisResult_neutralData_noWarning() = runTest {
        val fakeTestRepository = FakeTestRepository()
        val generator = DevGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        generator.generateData(DataGenerationType.AVERAGE_VISION)
        val gateway = AnalysisGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        val result = gateway.getAnalysisResult(parameters)

        assertFalse(result.showWarningAboutVision)
    }

    @Test
    fun getAnalysisResult_negativeData_warning() = runTest {
        val fakeTestRepository = FakeTestRepository()
        val generator = DevGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        generator.generateData(DataGenerationType.BAD_VISION)
        val gateway = AnalysisGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        val result = gateway.getAnalysisResult(parameters)

        assertTrue(result.showWarningAboutVision)
    }

    @Test
    fun getAnalysisResult_withNoise_detected() = runTest {
        val fakeTestRepository = FakeTestRepository()
        val generator = DevGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        generator.generateData(DataGenerationType.GOOD_VISION)
        val noiseTest = AcuityTestResult(
            id = 42,
            timestamp = currentTimeMillis() - 1000,
            symbolsType = AcuityTestSymbolsType.LETTERS_RU,
            testEyesType = TestEyesType.BOTH,
            resultLeftEye = 0,
            resultRightEye = 10
        )
        val gateway = AnalysisGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        val result1 = gateway.getAnalysisResult(parameters)
        fakeTestRepository.add(noiseTest)
        val result2 = gateway.getAnalysisResult(parameters)

        assertFalse(result1.lastResultRecognizedAsNoise)
        assertTrue(result2.lastResultRecognizedAsNoise)
    }

    @Test
    fun getAnalysisResult_consolidated_withAllTests() = runTest {
        val fakeTestRepository = FakeTestRepository()
        val generator = DevGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        generator.generateData(DataGenerationType.GOOD_VISION)
        generator.generateData(DataGenerationType.OTHER_TESTS)
        val gateway = AnalysisGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        val result = gateway.getAnalysisResult(parameters)
        val tests = fakeTestRepository.getListDistinctByType()

        assertEquals(tests.size, result.testResults.size)
    }

    @Test
    fun getAnalysisResult_acuityOnly_withoutAllTests() = runTest {
        val fakeTestRepository = FakeTestRepository()
        val generator = DevGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        generator.generateData(DataGenerationType.GOOD_VISION)
        generator.generateData(DataGenerationType.OTHER_TESTS)
        val gateway = AnalysisGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = currentTimeMillis(),
            analysisType = AnalysisType.ACUITY_ONLY,
            applyDynamicCorrections = true
        )

        val result = gateway.getAnalysisResult(parameters)

        assertTrue(result.testResults.isEmpty())
    }

    @Test
    fun getAnalysisResult_smallData_twoGroups() = runTest {
        val fakeTestRepository = FakeTestRepository()
        val generator = DevGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        generator.generateData(DataGenerationType.GOOD_VISION)
        val gateway = AnalysisGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        val lastTime = fakeTestRepository.tests.maxOf { it.timestamp }
        val parameters = AnalysisParameters(
            dateFrom = lastTime - 10 * GlobalConstants.DAY_MS,
            dateTo = currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        val result = gateway.getAnalysisResult(parameters)

        assertEquals(2, result.leftEyeAnalysisResult.chartData.size)
        assertEquals(2, result.rightEyeAnalysisResult.chartData.size)
    }

    @Test
    fun getAnalysisResult_bigData_manyGroups() = runTest {
        val fakeTestRepository = FakeTestRepository()
        val generator = DevGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        generator.generateData(DataGenerationType.GOOD_VISION)
        val gateway = AnalysisGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        val result = gateway.getAnalysisResult(parameters)

        assertTrue(result.leftEyeAnalysisResult.chartData.size > 5)
        assertTrue(result.rightEyeAnalysisResult.chartData.size > 5)
    }

    @Test
    fun getAnalysisResult_actualData_withExtrapolation() = runTest {
        val fakeTestRepository = FakeTestRepository()
        val generator = DevGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        generator.generateData(DataGenerationType.GOOD_VISION)
        val gateway = AnalysisGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        val result = gateway.getAnalysisResult(parameters)

        assertNotNull(result.leftEyeAnalysisResult.extrapolatedResult)
        assertNotNull(result.rightEyeAnalysisResult.extrapolatedResult)
    }

    @Test
    fun getAnalysisResult_oldData_withoutExtrapolation() = runTest {
        val fakeTestRepository = FakeTestRepository()
        val generator = DevGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        generator.generateData(DataGenerationType.GOOD_VISION)
        val gateway = AnalysisGatewayImpl(
            testRepository = fakeTestRepository,
            clock = Clock.systemUTC()
        )
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        val timeOffset = 100 * GlobalConstants.DAY_MS
        fakeTestRepository.tests.forEach {
            it.timestamp -= timeOffset
        }
        val result = gateway.getAnalysisResult(parameters)

        assertNull(result.leftEyeAnalysisResult.extrapolatedResult)
        assertNull(result.rightEyeAnalysisResult.extrapolatedResult)
    }
}