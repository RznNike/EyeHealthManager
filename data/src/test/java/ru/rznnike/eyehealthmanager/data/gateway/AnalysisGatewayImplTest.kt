package ru.rznnike.eyehealthmanager.data.gateway

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.rznnike.eyehealthmanager.domain.model.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.AnalysisParameters
import ru.rznnike.eyehealthmanager.domain.model.TestResult
import ru.rznnike.eyehealthmanager.domain.model.TestResultPagingParameters
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.enums.AnalysisType
import ru.rznnike.eyehealthmanager.domain.model.enums.DataGenerationType
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType
import ru.rznnike.eyehealthmanager.domain.model.exception.NotEnoughDataException
import ru.rznnike.eyehealthmanager.domain.storage.repository.TestRepository
import java.util.TimeZone

class AnalysisGatewayImplTest {
    private val fakeTestRepository = object : TestRepository {
        val tests = mutableListOf<TestResult>()

        override suspend fun getTests(parameters: TestResultPagingParameters) = tests
            .filter { testResult ->
                parameters.filter?.let { filter ->
                    ((!filter.filterByDate) || LongRange(filter.dateFrom, filter.dateTo).contains(testResult.timestamp))
                            && ((!filter.filterByType) || (testResult is AcuityTestResult))
                } ?: true
            }

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
    private val generator = DevGatewayImpl(fakeTestRepository)

    @BeforeEach
    fun beforeEach() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Test
    fun getAnalysisResult_noData_exception() = runTest {
        fakeTestRepository.deleteAllTests()
        val gateway = AnalysisGatewayImpl(testRepository = fakeTestRepository)
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = System.currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        assertThrows<NotEnoughDataException> {
            gateway.getAnalysisResult(parameters)
        }
    }

    @Test
    fun getAnalysisResult_notEnoughData_exception() = runTest {
        fakeTestRepository.deleteAllTests()
        fakeTestRepository.addTest(
            AcuityTestResult()
        )
        val gateway = AnalysisGatewayImpl(testRepository = fakeTestRepository)
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = System.currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        assertThrows<NotEnoughDataException> {
            gateway.getAnalysisResult(parameters)
        }
    }

    @Test
    fun getAnalysisResult_positiveData_noWarning() = runTest {
        fakeTestRepository.deleteAllTests()
        generator.generateData(DataGenerationType.GOOD_VISION)
        val gateway = AnalysisGatewayImpl(testRepository = fakeTestRepository)
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = System.currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        val result = gateway.getAnalysisResult(parameters)

        assert(!result.showWarningAboutVision)
    }

    @Test
    fun getAnalysisResult_neutralData_noWarning() = runTest {
        fakeTestRepository.deleteAllTests()
        generator.generateData(DataGenerationType.AVERAGE_VISION)
        val gateway = AnalysisGatewayImpl(testRepository = fakeTestRepository)
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = System.currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        val result = gateway.getAnalysisResult(parameters)

        assert(!result.showWarningAboutVision)
    }

    @Test
    fun getAnalysisResult_negativeData_warning() = runTest {
        fakeTestRepository.deleteAllTests()
        generator.generateData(DataGenerationType.BAD_VISION)
        val gateway = AnalysisGatewayImpl(testRepository = fakeTestRepository)
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = System.currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        val result = gateway.getAnalysisResult(parameters)

        assert(result.showWarningAboutVision)
    }

    @Test
    fun getAnalysisResult_withNoise_detected() = runTest {
        fakeTestRepository.deleteAllTests()
        generator.generateData(DataGenerationType.GOOD_VISION)
        val noiseTest = AcuityTestResult(
            id = 42,
            timestamp = System.currentTimeMillis() - 1000,
            symbolsType = AcuityTestSymbolsType.LETTERS_RU,
            testEyesType = TestEyesType.BOTH,
            resultLeftEye = 0,
            resultRightEye = 10
        )
        val gateway = AnalysisGatewayImpl(testRepository = fakeTestRepository)
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = System.currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        val result1 = gateway.getAnalysisResult(parameters)
        fakeTestRepository.addTest(noiseTest)
        val result2 = gateway.getAnalysisResult(parameters)

        assert(!result1.lastResultRecognizedAsNoise)
        assert(result2.lastResultRecognizedAsNoise)
    }

    @Test
    fun getAnalysisResult_consolidated_withAllTests() = runTest {
        fakeTestRepository.deleteAllTests()
        generator.generateData(DataGenerationType.GOOD_VISION)
        generator.generateData(DataGenerationType.OTHER_TESTS)
        val gateway = AnalysisGatewayImpl(testRepository = fakeTestRepository)
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = System.currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        val result = gateway.getAnalysisResult(parameters)
        val tests = fakeTestRepository.getAllLastTests()

        assertEquals(tests.size, result.testResults.size)
    }

    @Test
    fun getAnalysisResult_acuityOnly_withoutAllTests() = runTest {
        fakeTestRepository.deleteAllTests()
        generator.generateData(DataGenerationType.GOOD_VISION)
        generator.generateData(DataGenerationType.OTHER_TESTS)
        val gateway = AnalysisGatewayImpl(testRepository = fakeTestRepository)
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = System.currentTimeMillis(),
            analysisType = AnalysisType.ACUITY_ONLY,
            applyDynamicCorrections = true
        )

        val result = gateway.getAnalysisResult(parameters)

        assert(result.testResults.isEmpty())
    }

    @Test
    fun getAnalysisResult_smallData_twoGroups() = runTest {
        fakeTestRepository.deleteAllTests()
        generator.generateData(DataGenerationType.GOOD_VISION)
        val gateway = AnalysisGatewayImpl(testRepository = fakeTestRepository)
        val lastTime = fakeTestRepository.tests.maxOf { it.timestamp }
        val parameters = AnalysisParameters(
            dateFrom = lastTime - 10 * 86400 * 1000L, // 10 days
            dateTo = System.currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        val result = gateway.getAnalysisResult(parameters)

        assertEquals(2, result.leftEyeAnalysisResult.chartData.size)
        assertEquals(2, result.rightEyeAnalysisResult.chartData.size)
    }

    @Test
    fun getAnalysisResult_bigData_manyGroups() = runTest {
        fakeTestRepository.deleteAllTests()
        generator.generateData(DataGenerationType.GOOD_VISION)
        val gateway = AnalysisGatewayImpl(testRepository = fakeTestRepository)
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = System.currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        val result = gateway.getAnalysisResult(parameters)

        assert(result.leftEyeAnalysisResult.chartData.size > 5)
        assert(result.rightEyeAnalysisResult.chartData.size > 5)
    }

    @Test
    fun getAnalysisResult_actualData_withExtrapolation() = runTest {
        fakeTestRepository.deleteAllTests()
        generator.generateData(DataGenerationType.GOOD_VISION)
        val gateway = AnalysisGatewayImpl(testRepository = fakeTestRepository)
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = System.currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        val result = gateway.getAnalysisResult(parameters)

        assertNotNull(result.leftEyeAnalysisResult.extrapolatedResult)
        assertNotNull(result.rightEyeAnalysisResult.extrapolatedResult)
    }

    @Test
    fun getAnalysisResult_oldData_withoutExtrapolation() = runTest {
        fakeTestRepository.deleteAllTests()
        generator.generateData(DataGenerationType.GOOD_VISION)
        val gateway = AnalysisGatewayImpl(testRepository = fakeTestRepository)
        val parameters = AnalysisParameters(
            dateFrom = 0,
            dateTo = System.currentTimeMillis(),
            analysisType = AnalysisType.CONSOLIDATED_REPORT,
            applyDynamicCorrections = true
        )

        val timeOffset = 100 * 86400 * 1000L // 100 days
        fakeTestRepository.tests.forEach {
            it.timestamp -= timeOffset
        }
        val result = gateway.getAnalysisResult(parameters)

        assertNull(result.leftEyeAnalysisResult.extrapolatedResult)
        assertNull(result.rightEyeAnalysisResult.extrapolatedResult)
    }
}