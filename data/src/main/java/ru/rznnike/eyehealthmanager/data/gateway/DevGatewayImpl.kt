package ru.rznnike.eyehealthmanager.data.gateway

import kotlinx.coroutines.withContext
import ru.rznnike.eyehealthmanager.data.utils.DataConstants
import ru.rznnike.eyehealthmanager.domain.gateway.DevGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.model.analysis.LinearFunction
import ru.rznnike.eyehealthmanager.domain.model.common.DataGenerationType
import ru.rznnike.eyehealthmanager.domain.model.common.DayPart
import ru.rznnike.eyehealthmanager.domain.model.test.TestEyesType
import ru.rznnike.eyehealthmanager.domain.model.test.TestResult
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.test.astigmatism.AstigmatismAnswerType
import ru.rznnike.eyehealthmanager.domain.model.test.astigmatism.AstigmatismTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.colorperception.ColorPerceptionTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.contrast.ContrastTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.daltonism.DaltonismAnomalyType
import ru.rznnike.eyehealthmanager.domain.model.test.daltonism.DaltonismTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.nearfar.NearFarAnswerType
import ru.rznnike.eyehealthmanager.domain.model.test.nearfar.NearFarTestResult
import ru.rznnike.eyehealthmanager.domain.storage.repository.TestRepository
import ru.rznnike.eyehealthmanager.domain.utils.millis
import ru.rznnike.eyehealthmanager.domain.utils.toLocalDate
import java.time.Clock
import kotlin.random.Random

class DevGatewayImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val testRepository: TestRepository,
    private val clock: Clock
) : DevGateway {
    override suspend fun generateData(type: DataGenerationType) = withContext(dispatcherProvider.io) {
        when (type) {
            DataGenerationType.GOOD_VISION -> generateAcuityTests(
                leftEyeTrend = LinearFunction(0.002, 0.7),
                rightEyeTrend = LinearFunction(0.002, 0.6)
            )
            DataGenerationType.AVERAGE_VISION -> generateAcuityTests(
                leftEyeTrend = LinearFunction(0.0, 0.9),
                rightEyeTrend = LinearFunction(0.0, 0.95)
            )
            DataGenerationType.BAD_VISION -> generateAcuityTests(
                leftEyeTrend = LinearFunction(-0.002, 0.7),
                rightEyeTrend = LinearFunction(-0.003, 0.7)
            )
            DataGenerationType.OTHER_TESTS -> generateOtherTests()
        }
    }

    private suspend fun generateAcuityTests(
        leftEyeTrend: LinearFunction,
        rightEyeTrend: LinearFunction
    ) {
        var dateTime = clock.millis().toLocalDate().minusDays(DataConstants.ANALYSIS_MAX_RANGE_DAYS).atStartOfDay()
        val tests = mutableListOf<TestResult>()
        for (day in 0 until DataConstants.ANALYSIS_MAX_RANGE_DAYS) {
            tests.add(
                AcuityTestResult(
                    timestamp = dateTime.millis() + Random.nextInt(30_000_000), // nearly first 8 hours of day
                    symbolsType = AcuityTestSymbolsType.LETTERS_RU,
                    testEyesType = TestEyesType.BOTH,
                    dayPart = DayPart.MIDDLE,
                    resultLeftEye = (leftEyeTrend.getY(day.toDouble()) * 100).toInt() + Random.nextInt(-3, 4),
                    resultRightEye = (rightEyeTrend.getY(day.toDouble()) * 100).toInt() + Random.nextInt(-3, 4),
                )
            )
            dateTime = dateTime.plusDays(1)
        }

        testRepository.add(tests)
    }

    private suspend fun generateOtherTests() {
        val timestamp = clock.millis()
        testRepository.add(
            listOf(
                AstigmatismTestResult(
                    timestamp = timestamp,
                    resultLeftEye = AstigmatismAnswerType.ANOMALY,
                    resultRightEye = AstigmatismAnswerType.OK
                ),
                NearFarTestResult(
                    timestamp = timestamp,
                    resultLeftEye = NearFarAnswerType.EQUAL,
                    resultRightEye = NearFarAnswerType.GREEN_BETTER
                ),
                ColorPerceptionTestResult(
                    timestamp = timestamp,
                    recognizedColorsCount = 35,
                    allColorsCount = 39
                ),
                DaltonismTestResult(
                    timestamp = timestamp,
                    errorsCount = 5,
                    anomalyType = DaltonismAnomalyType.DEITERANOPIA
                ),
                ContrastTestResult(
                    timestamp = timestamp,
                    recognizedContrast = 1
                )
            )
        )
    }
}