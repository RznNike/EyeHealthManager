package ru.rznnike.eyehealthmanager.data.gateway

import ru.rznnike.eyehealthmanager.domain.storage.repository.TestRepository
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
import ru.rznnike.eyehealthmanager.domain.utils.getTodayCalendar
import java.util.Calendar
import kotlin.random.Random

class DevGatewayImpl(
    private val testRepository: TestRepository
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
        val days = 90
        val calendar = getTodayCalendar().apply {
            add(Calendar.MONTH, -3)
        }
        val tests = mutableListOf<TestResult>()
        for (day in 0 until days) {
            tests.add(
                AcuityTestResult(
                    timestamp = calendar.timeInMillis + Random.nextInt(30_000_000), // nearly first 8 hours of day
                    symbolsType = AcuityTestSymbolsType.LETTERS_RU,
                    testEyesType = TestEyesType.BOTH,
                    dayPart = DayPart.MIDDLE,
                    resultLeftEye = (leftEyeTrend.getY(day.toDouble()) * 100).toInt() + Random.nextInt(-3, 4),
                    resultRightEye = (rightEyeTrend.getY(day.toDouble()) * 100).toInt() + Random.nextInt(-3, 4),
                )
            )
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        testRepository.addTests(tests)
    }

    private suspend fun generateOtherTests() {
        val timestamp = System.currentTimeMillis()
        testRepository.addTests(
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