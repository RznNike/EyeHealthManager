package ru.rznnike.eyehealthmanager.data.gateway

import ru.rznnike.eyehealthmanager.domain.gateway.DevGateway
import ru.rznnike.eyehealthmanager.domain.model.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.AstigmatismTestResult
import ru.rznnike.eyehealthmanager.domain.model.ColorPerceptionTestResult
import ru.rznnike.eyehealthmanager.domain.model.ContrastTestResult
import ru.rznnike.eyehealthmanager.domain.model.DaltonismTestResult
import ru.rznnike.eyehealthmanager.domain.model.LinearFunction
import ru.rznnike.eyehealthmanager.domain.model.NearFarTestResult
import ru.rznnike.eyehealthmanager.domain.model.TestResult
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.enums.AstigmatismAnswerType
import ru.rznnike.eyehealthmanager.domain.model.enums.DaltonismAnomalyType
import ru.rznnike.eyehealthmanager.domain.model.enums.DataGenerationType
import ru.rznnike.eyehealthmanager.domain.model.enums.DayPart
import ru.rznnike.eyehealthmanager.domain.model.enums.NearFarAnswerType
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType
import ru.rznnike.eyehealthmanager.domain.storage.repository.TestRepository
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.millis
import ru.rznnike.eyehealthmanager.domain.utils.toLocalDate
import java.time.Clock
import kotlin.random.Random

class DevGatewayImpl(
    private val testRepository: TestRepository,
    private val clock: Clock
) : DevGateway {
    override suspend fun generateData(type: DataGenerationType) {
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
        var dateTime = clock.millis().toLocalDate().minusDays(GlobalConstants.ANALYSIS_MAX_RANGE_DAYS).atStartOfDay()
        val tests = mutableListOf<TestResult>()
        for (day in 0 until GlobalConstants.ANALYSIS_MAX_RANGE_DAYS) {
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