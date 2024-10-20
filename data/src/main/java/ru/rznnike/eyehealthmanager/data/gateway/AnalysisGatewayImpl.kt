package ru.rznnike.eyehealthmanager.data.gateway

import kotlinx.coroutines.withContext
import ru.rznnike.eyehealthmanager.data.utils.DataConstants
import ru.rznnike.eyehealthmanager.domain.gateway.AnalysisGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.model.analysis.AnalysisParameters
import ru.rznnike.eyehealthmanager.domain.model.analysis.AnalysisResult
import ru.rznnike.eyehealthmanager.domain.model.analysis.AnalysisStatistics
import ru.rznnike.eyehealthmanager.domain.model.analysis.AnalysisType
import ru.rznnike.eyehealthmanager.domain.model.analysis.DynamicCorrectionsData
import ru.rznnike.eyehealthmanager.domain.model.analysis.EyeChartPoint
import ru.rznnike.eyehealthmanager.domain.model.analysis.FunctionPoint
import ru.rznnike.eyehealthmanager.domain.model.analysis.LinearFunction
import ru.rznnike.eyehealthmanager.domain.model.analysis.SingleEyeAnalysisResult
import ru.rznnike.eyehealthmanager.domain.model.analysis.VisionDynamicType
import ru.rznnike.eyehealthmanager.domain.model.common.DayPart
import ru.rznnike.eyehealthmanager.domain.model.exception.NotEnoughDataException
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultFilter
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultPagingParameters
import ru.rznnike.eyehealthmanager.domain.model.test.TestEyesType
import ru.rznnike.eyehealthmanager.domain.model.test.TestType
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestResultGroup
import ru.rznnike.eyehealthmanager.domain.storage.repository.TestRepository
import java.time.Clock
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToLong

private const val WARNING_VISION_DIFFERENCE_THRESHOLD = 20
private const val VISION_DYNAMIC_TYPE_THRESHOLD = 5
private const val NOISE_MIN_POINTS_COUNT = 5
private const val NOISE_MIN_DATE_DELTA_MS = 5 * DataConstants.DAY_MS
private const val NOISE_MAX_DATE_DELTA_MS = 10 * DataConstants.DAY_MS
private const val NOISE_FILTER_THRESHOLD = 30
private const val EXTRAPOLATION_MAX_DATE_DELTA_MS = 90 * DataConstants.DAY_MS
private const val EXTRAPOLATION_RESULT_DATE_DIVIDER = 3
private const val CORRECTIONS_DATE_DELTA_MS = 7 * DataConstants.DAY_MS

class AnalysisGatewayImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val testRepository: TestRepository,
    private val clock: Clock
) : AnalysisGateway {
    override suspend fun getAnalysisResult(parameters: AnalysisParameters): AnalysisResult = withContext(dispatcherProvider.io) {
        val acuitySearchParameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = true,
                filterByType = true,
                dateFrom = parameters.dateFrom,
                dateTo = parameters.dateTo,
                selectedTestTypes = mutableListOf(TestType.ACUITY)
            )
        )

        val acuityResults = testRepository.getList(acuitySearchParameters)

        if (acuityResults.size < DataConstants.ANALYSIS_MIN_RESULTS_COUNT) {
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
        if (parameters.applyDynamicCorrections) {
            applyDynamicCorrections(
                sortedResults,
                leftEyeDynamicCorrections,
                rightEyeDynamicCorrections
            )
        }

        val lastResult = sortedResults.last()
        removeNoises(sortedResults)
        val lastResultRecognizedAsNoise = !sortedResults.contains(lastResult)

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

        val allLastResults = if (parameters.analysisType == AnalysisType.CONSOLIDATED_REPORT) {
            testRepository.getListDistinctByType().filter { it !is AcuityTestResult }
        } else {
            emptyList()
        }
        val showWarningAboutVision = checkShowWarningAboutVision(
            leftEyeData = leftEyeAnalysisResult.statistics,
            rightEyeData = rightEyeAnalysisResult.statistics
        )

        AnalysisResult(
            testResults = allLastResults,
            leftEyeAnalysisResult = leftEyeAnalysisResult,
            rightEyeAnalysisResult = rightEyeAnalysisResult,
            showWarningAboutVision = showWarningAboutVision,
            lastResultRecognizedAsNoise = lastResultRecognizedAsNoise
        )
    }

    private fun getDynamicCorrections(
        data: MutableList<AcuityTestResult>,
        eye: TestEyesType
    ): DynamicCorrectionsData {
        val beginningCorrections = mutableListOf<Double>()
        val endCorrections = mutableListOf<Double>()
        val doctorCorrections = mutableListOf<Double>()
        val intervalBeginningResults = mutableListOf<Int>()
        val intervalMiddleResults = mutableListOf<Int>()
        val intervalEndResults = mutableListOf<Int>()
        val intervalDoctorResults = mutableListOf<Int>()
        var startTimestamp = data.first().timestamp
        var endTimestamp = data.first().timestamp

        data.forEachIndexed { index, item ->
            if (eye == TestEyesType.LEFT) {
                item.resultLeftEye
            } else {
                item.resultRightEye
            }?.let {
                when {
                    item.measuredByDoctor -> intervalDoctorResults.add(it)
                    item.dayPart == DayPart.BEGINNING -> intervalBeginningResults.add(it)
                    item.dayPart == DayPart.MIDDLE -> intervalMiddleResults.add(it)
                    item.dayPart == DayPart.END -> intervalEndResults.add(it)
                }
                endTimestamp = item.timestamp
            }
            if (((endTimestamp - startTimestamp) > CORRECTIONS_DATE_DELTA_MS) || (index == data.lastIndex)) {
                val averageDoctor = if (intervalDoctorResults.isNotEmpty()) intervalDoctorResults.average() else null
                val averageBeginning = if (intervalBeginningResults.isNotEmpty()) intervalBeginningResults.average() else null
                val averageMiddle = if (intervalMiddleResults.isNotEmpty()) intervalMiddleResults.average() else null
                val averageEnd = if (intervalEndResults.isNotEmpty()) intervalEndResults.average() else null

                averageMiddle?.let {
                    averageBeginning?.let {
                        beginningCorrections.add(1 - averageBeginning / averageMiddle)
                    }
                    averageEnd?.let {
                        endCorrections.add(1 - averageEnd / averageMiddle)
                    }
                } ?: run {
                    if ((averageBeginning != null) && (averageEnd != null)) {
                        val middle = averageBeginning + averageEnd / 2
                        beginningCorrections.add(1 - averageBeginning / middle)
                        endCorrections.add(1 - averageEnd / middle)
                    }
                }
                averageDoctor?.let {
                    val averages = listOfNotNull(
                        averageBeginning,
                        averageMiddle,
                        averageEnd
                    )
                    if (averages.isNotEmpty()) {
                        val averageTotal = averages.average()
                        doctorCorrections.add(averageDoctor / averageTotal - 1)
                    }
                }

                if (index < data.lastIndex) {
                    startTimestamp = data[index + 1].timestamp
                }
                intervalDoctorResults.clear()
                intervalBeginningResults.clear()
                intervalMiddleResults.clear()
                intervalEndResults.clear()
            }
        }

        val middle = if (doctorCorrections.isEmpty()) 0.0 else doctorCorrections.average()
        val beginning = if (beginningCorrections.isEmpty()) {
            middle
        } else {
            (1 + beginningCorrections.average()) * (1 + middle) - 1
        }
        val end = if (endCorrections.isEmpty()) {
            middle
        } else {
            (1 + endCorrections.average()) * (1 + middle) - 1
        }

        return DynamicCorrectionsData(
            beginning = beginning,
            middle = middle,
            end = end,
            doctorCorrectionsCalculated = doctorCorrections.isNotEmpty()
        )
    }

    private fun syncDynamicCorrections(
        leftEyeData: DynamicCorrectionsData,
        rightEyeData: DynamicCorrectionsData
    ) {
        when {
            leftEyeData.doctorCorrectionsCalculated && (!rightEyeData.doctorCorrectionsCalculated) -> {
                rightEyeData.middle = leftEyeData.middle
                rightEyeData.beginning = (1 + rightEyeData.beginning) * (1 + rightEyeData.middle) - 1
                rightEyeData.end = (1 + rightEyeData.end) * (1 + rightEyeData.middle) - 1
            }
            rightEyeData.doctorCorrectionsCalculated && (!leftEyeData.doctorCorrectionsCalculated) -> {
                leftEyeData.middle = rightEyeData.middle
                leftEyeData.beginning = (1 + leftEyeData.beginning) * (1 + leftEyeData.middle) - 1
                leftEyeData.end = (1 + leftEyeData.end) * (1 + leftEyeData.middle) - 1
            }
        }
    }

    private fun applyDynamicCorrections(
        data: MutableList<AcuityTestResult>,
        leftEyeData: DynamicCorrectionsData,
        rightEyeData: DynamicCorrectionsData
    ) {
        fun getCorrection(eyeData: DynamicCorrectionsData, dayPart: DayPart) =
            when (dayPart) {
                DayPart.BEGINNING -> eyeData.beginning
                DayPart.MIDDLE -> eyeData.middle
                DayPart.END -> eyeData.end
            }

        data.forEach { dataElement ->
            val correctionLeft = getCorrection(leftEyeData, dataElement.dayPart)
            dataElement.resultLeftEye?.let {
                dataElement.resultLeftEye = (it * (1 + correctionLeft)).toInt()
            }

            val correctionRight = getCorrection(rightEyeData, dataElement.dayPart)
            dataElement.resultRightEye?.let {
                dataElement.resultRightEye = (it * (1 + correctionRight)).toInt()
            }
        }
    }

    private fun removeNoises(data: MutableList<AcuityTestResult>) {
        val iterator = data.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            var nearbyData = data.filter {
                ((abs(it.timestamp - item.timestamp)) <= NOISE_MIN_DATE_DELTA_MS) && (it != item)
            }
            if (nearbyData.size < NOISE_MIN_POINTS_COUNT) {
                nearbyData = data.filter {
                    ((abs(it.timestamp - item.timestamp)) <= NOISE_MAX_DATE_DELTA_MS) && (it != item)
                }
            }
            if (nearbyData.size >= NOISE_MIN_POINTS_COUNT) {
                if (isPointNoise(item, nearbyData)) {
                    iterator.remove()
                }
            }
        }
    }

    private fun isPointNoise(
        analysePoint: AcuityTestResult,
        nearbyData: List<AcuityTestResult>
    ): Boolean {
        var smallDeltaCount = 0
        var bigDeltaCount = 0
        nearbyData.forEach {
            val deltaLeftEye = it.resultLeftEye?.let { result1 ->
                analysePoint.resultLeftEye?.let { result2 ->
                    abs(result1 - result2)
                }
            } ?: 0
            val deltaRightEye = it.resultRightEye?.let { result1 ->
                analysePoint.resultRightEye?.let { result2 ->
                    abs(result1 - result2)
                }
            } ?: 0
            if ((deltaLeftEye > NOISE_FILTER_THRESHOLD) || (deltaRightEye > NOISE_FILTER_THRESHOLD)) {
                bigDeltaCount++
            } else {
                smallDeltaCount++
            }
        }

        return bigDeltaCount >= smallDeltaCount
    }

    private fun getLinearTrend(points: List<FunctionPoint>) =
        if (points.size > 1) {
            val sumXY = points.sumOf { it.x * it.y }
            val sumX2 = points.sumOf { it.x * it.x }
            val averageX = points.map { it.x }.average()
            val averageY = points.map { it.y }.average()

            val a = (sumXY - points.size * averageX * averageY) / (sumX2 - points.size * averageX * averageX)
            val b = averageY - a * averageX

            LinearFunction(a, b)
        } else null

    private fun groupResults(data: List<AcuityTestResult>): List<AcuityTestResultGroup> {
        val groups = mutableListOf<AcuityTestResultGroup>()
        var currentGroup: AcuityTestResultGroup? = null
        data.forEach { item ->
            currentGroup = currentGroup ?: AcuityTestResultGroup(
                dateFrom = item.timestamp,
                dateTo = item.timestamp
            )
            currentGroup?.run {
                values.add(item)
                val groupIsFilled = ((values.size >= DataConstants.ANALYSIS_GROUPING_MIN_SIZE)
                        && ((item.timestamp - dateFrom) >= DataConstants.ANALYSIS_GROUPING_MIN_RANGE_MS))
                        || ((item.timestamp - dateFrom) > DataConstants.ANALYSIS_GROUPING_MAX_RANGE_MS)
                if (groupIsFilled) {
                    groups.add(this)
                    currentGroup = null
                }
            }
        }
        currentGroup?.let {
            groups.add(it)
        }

        var index = 0
        while (index < groups.size) {
            val group = groups[index]
            if ((group.values.size < DataConstants.ANALYSIS_GROUPING_MIN_SIZE) && (groups.size > 1)) {
                val previousGroup = groups.getOrNull(index - 1)
                val nextGroup = groups.getOrNull(index + 1)
                when {
                    previousGroup == null -> nextGroup
                    nextGroup == null -> previousGroup
                    previousGroup.values.size < nextGroup.values.size -> previousGroup
                    else -> nextGroup
                }?.let { groupToMerge ->
                    groupToMerge.apply {
                        dateFrom = min(dateFrom, group.dateFrom)
                        dateTo = max(dateTo, group.dateTo)
                        values.addAll(group.values)
                    }
                    groups.remove(group)
                }
            } else {
                index++
            }
        }

        if (groups.size < DataConstants.ANALYSIS_MIN_GROUPS_COUNT) {
            throw NotEnoughDataException()
        }
        return groups
    }

    private fun analyseEyeVision(
        groupedResults: List<AcuityTestResultGroup>,
        eye: TestEyesType
    ): SingleEyeAnalysisResult {
        val chartData = groupedResults.map { group ->
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
        return SingleEyeAnalysisResult(
            chartData = chartData,
            extrapolatedResult = findExtrapolatedResult(chartData),
            statistics = getStatistics(groupedResults, chartData)
        )
    }

    private fun findExtrapolatedResult(chartData: List<EyeChartPoint>): EyeChartPoint? {
        if (chartData.size > 1) {
            val lastData = chartData.maxBy { it.timestamp }
            val extrapolationPoints = chartData
                .filter { (lastData.timestamp - it.timestamp) < EXTRAPOLATION_MAX_DATE_DELTA_MS }
                .map {
                    FunctionPoint(
                        x = it.timestamp.toDouble(),
                        y = it.value.toDouble()
                    )
                }

            if (extrapolationPoints.size > 1) {
                val minDate = extrapolationPoints.minOf { it.x }
                val maxDate = extrapolationPoints.maxOf { it.x }
                if (((maxDate - minDate) / 2) > (clock.millis() - maxDate)) {
                    val extrapolationDate = clock.millis() + (maxDate - minDate) / EXTRAPOLATION_RESULT_DATE_DIVIDER
                    getLinearTrend(extrapolationPoints)?.let { trend ->
                        return EyeChartPoint(
                            timestamp = extrapolationDate.roundToLong(),
                            value = trend.getY(extrapolationDate).toInt()
                        )
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
            0 -> return null
            1 -> {
                visionDynamicValue = 0
                visionAnalysisPeriod = groupedResults.lastOrNull()?.let { it.dateTo - it.dateFrom } ?: 0
            }
            else -> {
                val lastTimestamp = groupedResults.last().dateTo
                val lastGroups = groupedResults.filter {
                    (lastTimestamp - it.dateFrom) < DataConstants.ANALYSIS_MAX_RANGE_MS
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
            visionDynamicValue <= -VISION_DYNAMIC_TYPE_THRESHOLD -> VisionDynamicType.DECREASE
            visionDynamicValue >= VISION_DYNAMIC_TYPE_THRESHOLD -> VisionDynamicType.INCREASE
            else -> VisionDynamicType.SAME
        }
        return AnalysisStatistics(
            visionDynamicType = visionDynamicType,
            visionDynamicValue = visionDynamicValue,
            visionAverageValue = chartData.lastOrNull()?.value ?: 0,
            analysisPeriod = visionAnalysisPeriod
        )
    }

    private fun checkShowWarningAboutVision(
        leftEyeData: AnalysisStatistics?,
        rightEyeData: AnalysisStatistics?
    ) = (abs((leftEyeData?.visionAverageValue ?: 0) - (rightEyeData?.visionAverageValue ?: 0)) > WARNING_VISION_DIFFERENCE_THRESHOLD)
            || (leftEyeData?.visionDynamicType == VisionDynamicType.DECREASE)
            || (rightEyeData?.visionDynamicType == VisionDynamicType.DECREASE)
}
