package ru.rznnike.eyehealthmanager.data.gateway

import ru.rznnike.eyehealthmanager.data.storage.repository.TestRepository
import ru.rznnike.eyehealthmanager.domain.gateway.AnalysisGateway
import ru.rznnike.eyehealthmanager.domain.model.*
import ru.rznnike.eyehealthmanager.domain.model.enums.*
import ru.rznnike.eyehealthmanager.domain.model.exception.NotEnoughDataException
import kotlin.math.abs

private const val WARNING_VISION_DIFFERENCE_THRESHOLD = 20
private const val VISION_DYNAMIC_TYPE_THRESHOLD = 5
private const val NOISE_MIN_POINTS_COUNT = 5
private const val NOISE_MIN_DATE_DELTA_MS = 3 * 86400 * 1000L // 3 days
private const val NOISE_MAX_DATE_DELTA_MS = 7 * 86400 * 1000L // 7 days
private const val NOISE_FILTER_THRESHOLD = 30
private const val EXTRAPOLATION_MAX_DATE_DELTA_MS = 90 * 86400 * 1000L // 90 days
private const val EXTRAPOLATION_RESULT_DATE_DIVIDER = 3
private const val CORRECTIONS_DATE_DELTA_MS = 7 * 86400 * 1000L // 7 days
private const val GROUPING_MIN_DATE_MS = 3 * 86400 * 1000L // 3 days
private const val GROUPING_MAX_DATE_MS = 14 * 86400 * 1000L // 14 days
private const val GROUPING_MIN_SIZE = 5
private const val STATISTICS_MAX_DATE_MS = 90 * 86400 * 1000L // 90 days
private const val MIN_GROUPS_COUNT = 2
private const val MIN_RESULTS_COUNT = GROUPING_MIN_SIZE * MIN_GROUPS_COUNT

class AnalysisGatewayImpl(
    private val testRepository: TestRepository
) : AnalysisGateway {
    override suspend fun getAnalysisResult(params: AnalysisParams): AnalysisResult {
        val acuitySearchParams = TestResultPagingParams(
            offset = 0,
            limit = Int.MAX_VALUE,
            filterParams = TestResultFilterParams(
                filterByDate = true,
                filterByType = true,
                dateFrom = params.dateFrom,
                dateTo = params.dateTo,
                selectedTestTypes = mutableListOf(TestType.ACUITY)
            )
        )

        val acuityResults = testRepository.getTests(acuitySearchParams)
        val allLastResults = testRepository.getAllLastTests()

        if (acuityResults.size < MIN_RESULTS_COUNT) {
            throw NotEnoughDataException()
        }

        val sortedResults = acuityResults
            .filterIsInstance<AcuityTestResult>()
            .sortedBy { it.timestamp }
            .toMutableList()

        val leftEyeDynamicCorrections = getDynamicCorrections(
            sortedResults,
            TestEyesType.LEFT
        )
        val rightEyeDynamicCorrections = getDynamicCorrections(
            sortedResults,
            TestEyesType.RIGHT
        )
        syncDynamicCorrections(leftEyeDynamicCorrections, rightEyeDynamicCorrections)
        if (params.applyDynamicCorrections) {
            applyDynamicCorrections(
                sortedResults,
                leftEyeDynamicCorrections,
                rightEyeDynamicCorrections
            )
        }

        removeNoises(sortedResults)
        val lastResultRecognizedAsNoise = !sortedResults.contains(acuityResults.lastOrNull())

        val groupedResults = groupResults(sortedResults)

        val leftEyeAnalysisResult = analyseEyeVision(
            groupedResults,
            TestEyesType.LEFT
        ).apply {
            dynamicCorrections = leftEyeDynamicCorrections
        }
        val rightEyeAnalysisResult = analyseEyeVision(
            groupedResults,
            TestEyesType.RIGHT
        ).apply {
            dynamicCorrections = rightEyeDynamicCorrections
        }

        return AnalysisResult(
            testResults = if (params.analysisType == AnalysisType.CONSOLIDATED_REPORT) {
                allLastResults.filter { it !is AcuityTestResult }
            } else {
                listOf()
            },
            leftEyeAnalysisResult = leftEyeAnalysisResult,
            rightEyeAnalysisResult = rightEyeAnalysisResult,
            showWarningAboutVision = checkShowWarningAboutVision(leftEyeAnalysisResult, rightEyeAnalysisResult),
            lastResultRecognizedAsNoise = lastResultRecognizedAsNoise
        )
    }

    private fun getDynamicCorrections(
        data: MutableList<AcuityTestResult>,
        eye: TestEyesType
    ): DynamicCorrectionsData {
        var beginningCorrectionsSum = 0.0
        var beginningCorrectionsCount = 0
        var endCorrectionsSum = 0.0
        var endCorrectionsCount = 0
        var doctorCorrectionsSum = 0.0
        var doctorCorrectionsCount = 0
        var intervalBeginningSum = 0
        var intervalBeginningCount = 0
        var intervalMiddleSum = 0
        var intervalMiddleCount = 0
        var intervalEndSum = 0
        var intervalEndCount = 0
        var intervalSumDoctor = 0
        var intervalCountDoctor = 0
        var startTimestamp = data.first().timestamp
        var endTimestamp = data.first().timestamp
        var index = 0
        while (index < data.size) {
            val item = data[index]
            val value = if (eye == TestEyesType.LEFT) item.resultLeftEye else item.resultRightEye
            value?.let {
                if (item.measuredByDoctor) {
                    intervalSumDoctor += it
                    intervalCountDoctor++
                } else {
                    when (item.dayPart) {
                        DayPart.BEGINNING -> {
                            intervalBeginningSum += it
                            intervalBeginningCount++
                        }
                        DayPart.MIDDLE -> {
                            intervalMiddleSum += it
                            intervalMiddleCount++
                        }
                        DayPart.END -> {
                            intervalEndSum += it
                            intervalEndCount++
                        }
                    }
                }
                endTimestamp = item.timestamp
            }
            if (((endTimestamp - startTimestamp) > CORRECTIONS_DATE_DELTA_MS) || (index == (data.size - 1))) {
                val averageBeginning = if (intervalBeginningCount > 0) intervalBeginningSum.toDouble() / intervalBeginningCount else null
                val averageMiddle = if (intervalMiddleCount > 0) intervalMiddleSum.toDouble() / intervalMiddleCount else null
                val averageEnd = if (intervalEndCount > 0) intervalEndSum.toDouble() / intervalEndCount else null
                val averageDoctor = if (intervalCountDoctor > 0) intervalSumDoctor.toDouble() / intervalCountDoctor else null

                if (averageMiddle == null) {
                    if ((averageBeginning != null) && (averageEnd != null)) {
                        val middle = averageBeginning + averageEnd / 2
                        beginningCorrectionsSum += 1 - averageBeginning / middle
                        beginningCorrectionsCount++
                        endCorrectionsSum += 1 - averageEnd / middle
                        endCorrectionsCount++
                    }
                } else {
                    averageBeginning?.let {
                        beginningCorrectionsSum += 1 - averageBeginning / averageMiddle
                        beginningCorrectionsCount++
                    }
                    averageEnd?.let {
                        endCorrectionsSum += 1 - averageEnd / averageMiddle
                        endCorrectionsCount++
                    }
                }
                averageDoctor?.let {
                    var averageTotalSum = 0.0
                    var averageTotalCount = 0
                    averageBeginning?.let {
                        averageTotalSum += it
                        averageTotalCount++
                    }
                    averageMiddle?.let {
                        averageTotalSum += it
                        averageTotalCount++
                    }
                    averageEnd?.let {
                        averageTotalSum += it
                        averageTotalCount++
                    }
                    if (averageTotalCount > 0) {
                        val averageTotal = averageTotalSum / averageTotalCount
                        doctorCorrectionsSum += averageDoctor / averageTotal - 1
                        doctorCorrectionsCount++
                    }
                }

                if (index < (data.size - 1)) {
                    startTimestamp = data[index + 1].timestamp
                }
                intervalBeginningSum = 0
                intervalBeginningCount = 0
                intervalMiddleSum = 0
                intervalMiddleCount = 0
                intervalEndSum = 0
                intervalEndCount = 0
                intervalSumDoctor = 0
                intervalCountDoctor = 0
            }
            index++
        }

        val middle = if (doctorCorrectionsCount > 0) {
            doctorCorrectionsSum / doctorCorrectionsCount
        } else {
            0.0
        }
        val beginning = if (beginningCorrectionsCount > 0) {
            (1 + beginningCorrectionsSum / beginningCorrectionsCount) * (1 + middle) - 1
        } else {
            middle
        }
        val end = if (endCorrectionsCount > 0) {
            (1 + endCorrectionsSum / endCorrectionsCount) * (1 + middle) - 1
        } else {
            middle
        }

        return DynamicCorrectionsData(
            beginning = beginning,
            middle = middle,
            end = end,
            doctorCorrectionsCalculated = doctorCorrectionsCount > 0
        )
    }

    private fun syncDynamicCorrections(
        leftEyeDynamicCorrections: DynamicCorrectionsData,
        rightEyeDynamicCorrections: DynamicCorrectionsData
    ) {
        if (leftEyeDynamicCorrections.doctorCorrectionsCalculated xor rightEyeDynamicCorrections.doctorCorrectionsCalculated) {
            if (leftEyeDynamicCorrections.doctorCorrectionsCalculated) {
                rightEyeDynamicCorrections.middle = leftEyeDynamicCorrections.middle
                rightEyeDynamicCorrections.beginning = (1 + rightEyeDynamicCorrections.beginning) *
                        (1 + rightEyeDynamicCorrections.middle) - 1
                rightEyeDynamicCorrections.end = (1 + rightEyeDynamicCorrections.end) *
                        (1 + rightEyeDynamicCorrections.middle) - 1
            } else {
                leftEyeDynamicCorrections.middle = rightEyeDynamicCorrections.middle
                leftEyeDynamicCorrections.beginning = (1 + leftEyeDynamicCorrections.beginning) *
                        (1 + leftEyeDynamicCorrections.middle) - 1
                leftEyeDynamicCorrections.end = (1 + leftEyeDynamicCorrections.end) *
                        (1 + leftEyeDynamicCorrections.middle) - 1
            }
        }
    }

    private fun applyDynamicCorrections(
        data: MutableList<AcuityTestResult>,
        leftEyeDynamicCorrections: DynamicCorrectionsData,
        rightEyeDynamicCorrections: DynamicCorrectionsData
    ) {
        data.forEach { dataElement ->
            val correctionLeft: Double
            val correctionRight: Double
            when (dataElement.dayPart) {
                DayPart.BEGINNING -> {
                    correctionLeft = leftEyeDynamicCorrections.beginning
                    correctionRight = rightEyeDynamicCorrections.beginning
                }
                DayPart.MIDDLE -> {
                    correctionLeft = leftEyeDynamicCorrections.middle
                    correctionRight = rightEyeDynamicCorrections.middle
                }
                DayPart.END -> {
                    correctionLeft = leftEyeDynamicCorrections.end
                    correctionRight = rightEyeDynamicCorrections.end
                }
            }
            dataElement.resultLeftEye?.let {
                dataElement.resultLeftEye = (it * (1 + correctionLeft)).toInt()
            }
            dataElement.resultRightEye?.let {
                dataElement.resultRightEye = (it * (1 + correctionRight)).toInt()
            }
        }
    }

    private fun removeNoises(data: MutableList<AcuityTestResult>) {
        var index = 0
        while (index < data.size) {
            val item = data[index]
            var nearbyData = data.filter {
                ((abs(it.timestamp - item.timestamp)) <= NOISE_MIN_DATE_DELTA_MS)
                        && (it.timestamp != item.timestamp)
            }
            if (nearbyData.size < NOISE_MIN_POINTS_COUNT) {
                nearbyData = data.filter {
                    ((abs(it.timestamp - item.timestamp)) <= NOISE_MAX_DATE_DELTA_MS)
                            && (it.timestamp != item.timestamp)
                }
            }
            if (nearbyData.size >= NOISE_MIN_POINTS_COUNT) {
                val isPointNoise = isPointNoise(item, nearbyData, TestEyesType.LEFT)
                        || isPointNoise(item, nearbyData, TestEyesType.RIGHT)
                if (isPointNoise) {
                    data.remove(item)
                } else {
                    index++
                }
            } else {
                index++
            }
        }
    }

    private fun isPointNoise(
        analysePoint: AcuityTestResult,
        nearbyData: List<AcuityTestResult>,
        eye: TestEyesType
    ): Boolean {
        val functionPoints = nearbyData.mapNotNull { point ->
            val y = if (eye == TestEyesType.LEFT) point.resultLeftEye else point.resultRightEye
            y?.let {
                FunctionPoint(
                    x = point.timestamp.toDouble(),
                    y = y.toDouble()
                )
            }
        }
        val trend = getLinearTrend(functionPoints)
        val analyseX = analysePoint.timestamp.toDouble()
        val analyseY = if (eye == TestEyesType.LEFT) analysePoint.resultLeftEye else analysePoint.resultRightEye

        return (analyseY != null) && (trend != null) && (abs(analyseY - trend.getY(analyseX)) > NOISE_FILTER_THRESHOLD)
    }

    private fun getLinearTrend(points: List<FunctionPoint>): LinearFunction? {
        if (points.size < 2) {
            return null
        } else {
            var sumXY = 0.0
            var sumX2 = 0.0
            var sumX = 0.0
            var sumY = 0.0
            points.forEach {
                sumXY += it.x * it.y
                sumX2 += it.x * it.x
                sumX += it.x
                sumY += it.y
            }
            val averageX = sumX / points.size
            val averageY = sumY / points.size
            val a = (sumXY - points.size * averageX * averageY) / (sumX2 - points.size * averageX * averageX)
            val b = averageY - a * averageX

            return LinearFunction(a, b)
        }
    }

    private fun groupResults(data: List<AcuityTestResult>): List<AcuityTestResultGroup> {
        val groups = mutableListOf<AcuityTestResultGroup>()
        var currentGroup: AcuityTestResultGroup? = null
        var index = 0
        while (index < data.size) {
            val item = data[index]
            index++
            val groupIsFilled = (currentGroup != null)
                    && (((currentGroup.values.size >= GROUPING_MIN_SIZE) && ((item.timestamp - currentGroup.dateFrom) >= GROUPING_MIN_DATE_MS))
                        || ((item.timestamp - currentGroup.dateFrom) > GROUPING_MAX_DATE_MS))
            if ((currentGroup == null) || groupIsFilled) {
                currentGroup?.let {
                    groups.add(it)
                }
                currentGroup = AcuityTestResultGroup(
                    dateFrom = item.timestamp,
                    dateTo = item.timestamp
                )
            }
            currentGroup.values.add(item)
        }
        currentGroup?.let {
            groups.add(it)
        }

        index = 0
        while (index < groups.size) {
            val group = groups[index]
            if ((group.values.size < GROUPING_MIN_SIZE) && (groups.size > 1)) {
                val secondIndex = if (index > 0) {
                    if (index < (groups.size - 1)) {
                        if (groups[index - 1].values.size > groups[index + 1].values.size) {
                            index - 1
                        } else {
                            index + 1
                        }
                    } else {
                        index - 1
                    }
                } else {
                    index + 1
                }

                val mainGroup: AcuityTestResultGroup
                val subGroup: AcuityTestResultGroup
                if (index < secondIndex) {
                    mainGroup = groups[index]
                    subGroup = groups[secondIndex]
                } else {
                    mainGroup = groups[secondIndex]
                    subGroup = groups[index]
                }
                mainGroup.dateTo = subGroup.dateTo
                mainGroup.values.addAll(subGroup.values)
                groups.remove(subGroup)
            } else {
                index++
            }
        }

        if (groups.size < MIN_GROUPS_COUNT) {
            throw NotEnoughDataException()
        }
        return groups
    }

    private fun analyseEyeVision(
        groupedResults: List<AcuityTestResultGroup>,
        eye: TestEyesType
    ): SingleEyeAnalysisResult {
        val chartData = createChartData(groupedResults, eye)
        val extrapolatedResult = findExtrapolatedResult(chartData)
        val statistics = getStatistics(groupedResults, chartData)
        return SingleEyeAnalysisResult(
            chartData = chartData,
            extrapolatedResult = extrapolatedResult,
            statistics = statistics
        )
    }

    private fun createChartData(data: List<AcuityTestResultGroup>, eye: TestEyesType): List<EyeChartPoint> {
        return data.map { group ->
            EyeChartPoint(
                timestamp = group.dateTo,
                value = group.values
                    .mapNotNull {
                        if (eye == TestEyesType.LEFT) it.resultLeftEye else it.resultRightEye
                    }
                    .average()
                    .toInt()
            )
        }
    }

    private fun findExtrapolatedResult(chartData: List<EyeChartPoint>): EyeChartPoint? {
        if (chartData.size > 1) {
            val lastPoint = chartData.maxByOrNull { it.timestamp }
            lastPoint?.let {
                val filteredData = chartData
                    .filter { (lastPoint.timestamp - it.timestamp) < EXTRAPOLATION_MAX_DATE_DELTA_MS }
                    .map {
                        FunctionPoint(
                            x = it.timestamp.toDouble(),
                            y = it.value.toDouble()
                        )
                    }

                if (filteredData.size > 1) {
                    val minDate = filteredData.minByOrNull { it.x }?.x ?: 0.0
                    val maxDate = filteredData.maxByOrNull { it.x }?.x ?: 0.0
                    if (((maxDate - minDate) / 2) > (System.currentTimeMillis() - maxDate)) {
                        val extrapolationDate = System.currentTimeMillis() + (maxDate - minDate) / EXTRAPOLATION_RESULT_DATE_DIVIDER
                        getLinearTrend(filteredData)?.let { trend ->
                            return EyeChartPoint(
                                timestamp = extrapolationDate.toLong(),
                                value = trend.getY(extrapolationDate).toInt()
                            )
                        }
                    }
                }
            }

        }
        return null
    }

    private fun getStatistics(
        groupedResults: List<AcuityTestResultGroup>,
        chartData: List<EyeChartPoint>
    ): AnalysisStatistics? {
        val visionDynamicValue: Int
        val visionAnalysisPeriod: Long
        when (chartData.size) {
            0 -> {
                return null
            }
            1 -> {
                visionDynamicValue = 0
                visionAnalysisPeriod = groupedResults.lastOrNull()?.let {
                    it.dateTo - it.dateFrom
                } ?: 0
            }
            else -> {
                val lastTimestamp = groupedResults.last().dateTo
                val lastGroups = groupedResults.filter {
                    (lastTimestamp - it.dateFrom) < STATISTICS_MAX_DATE_MS
                }

                val averageValue = chartData
                    .subList(chartData.size - lastGroups.size, chartData.size)
                    .map { it.value }
                    .average()
                    .toInt()

                visionDynamicValue = chartData.last().value - averageValue
                visionAnalysisPeriod = lastGroups.last().dateTo - lastGroups.first().dateFrom
            }
        }

        val visionDynamicType = when {
            visionDynamicValue <= -VISION_DYNAMIC_TYPE_THRESHOLD -> {
                VisionDynamicType.DECREASE
            }
            visionDynamicValue >= VISION_DYNAMIC_TYPE_THRESHOLD -> {
                VisionDynamicType.INCREASE
            }
            else -> {
                VisionDynamicType.SAME
            }
        }
        return AnalysisStatistics(
            visionDynamicType = visionDynamicType,
            visionDynamicValue = visionDynamicValue,
            visionAverageValue = chartData.lastOrNull()?.value ?: 0,
            analysisPeriod = visionAnalysisPeriod
        )
    }

    private fun checkShowWarningAboutVision(
        leftEyeAnalysisResult: SingleEyeAnalysisResult,
        rightEyeAnalysisResult: SingleEyeAnalysisResult
    ): Boolean {
        return (abs((leftEyeAnalysisResult.statistics?.visionAverageValue ?: 0) - (rightEyeAnalysisResult.statistics?.visionAverageValue ?: 0)) >
                WARNING_VISION_DIFFERENCE_THRESHOLD)
                || (leftEyeAnalysisResult.statistics?.visionDynamicType == VisionDynamicType.DECREASE)
                || (rightEyeAnalysisResult.statistics?.visionDynamicType == VisionDynamicType.DECREASE)
    }
}
