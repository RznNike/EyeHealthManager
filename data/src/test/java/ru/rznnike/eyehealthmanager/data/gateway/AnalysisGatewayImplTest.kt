package ru.rznnike.eyehealthmanager.data.gateway

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.rznnike.eyehealthmanager.domain.model.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.AnalysisParameters
import ru.rznnike.eyehealthmanager.domain.model.TestResult
import ru.rznnike.eyehealthmanager.domain.model.TestResultPagingParameters
import ru.rznnike.eyehealthmanager.domain.model.enums.AnalysisType
import ru.rznnike.eyehealthmanager.domain.model.enums.DataGenerationType
import ru.rznnike.eyehealthmanager.domain.model.exception.NotEnoughDataException
import ru.rznnike.eyehealthmanager.domain.storage.repository.TestRepository
import java.util.TimeZone

class AnalysisGatewayImplTest {
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
        // TODO
    }

    @Test
    fun getAnalysisResult_consolidated_withAllTests() = runTest {
        // TODO
    }

    @Test
    fun getAnalysisResult_acuityOnly_withoutAllTests() = runTest {
        // TODO
    }

    @Test
    fun getAnalysisResult_smallData_twoGroups() = runTest {
        // TODO
    }

    @Test
    fun getAnalysisResult_bigData_manyGroups() = runTest {
        // TODO
    }

    @Test
    fun getAnalysisResult_actualData_withExtrapolation() = runTest {
        // TODO
    }

    @Test
    fun getAnalysisResult_oldData_withoutExtrapolation() = runTest {
        // TODO
    }
}