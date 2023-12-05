package ru.rznnike.eyehealthmanager.domain.model

import ru.rznnike.eyehealthmanager.domain.model.enums.DaltonismAnomalyType
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.toDate
import ru.rznnike.eyehealthmanager.domain.utils.toTimeStamp
import java.text.ParseException

class DaltonismTestResult(
    id: Long = 0,
    timestamp: Long,
    val errorsCount: Int,
    val anomalyType: DaltonismAnomalyType
) : TestResult(id, timestamp) {
    override fun exportToString(): String {
        return "%s\t%d\t%s".format(
            timestamp.toDate(GlobalConstants.DATE_PATTERN_FULL),
            errorsCount,
            anomalyType.toString()
        )
    }

    override fun contentEquals(other: TestResult?): Boolean {
        return (other is DaltonismTestResult)
                && (this.timestamp == other.timestamp)
                && (this.errorsCount == other.errorsCount)
                && (this.anomalyType == other.anomalyType)
    }

    companion object {
        const val EXPORT_HEADER = "timestamp\terrorsCount\tanomalyType"

        fun importFromString(string: String): DaltonismTestResult? {
            val stringParts = string.split("\t")
            return if (stringParts.size < 3) {
                null
            } else {
                val timestamp = try {
                    stringParts[0].toTimeStamp(GlobalConstants.DATE_PATTERN_FULL)
                } catch (e: ParseException) {
                    null
                }
                val errorsCount = stringParts[1].toIntOrNull()
                val anomalyType = DaltonismAnomalyType.parseName(stringParts[2])
                if ((timestamp == null)
                    || (errorsCount == null)
                    || (anomalyType == null)
                ) {
                    null
                } else {
                    DaltonismTestResult(
                        timestamp = timestamp,
                        errorsCount = errorsCount,
                        anomalyType = anomalyType
                    )
                }
            }
        }
    }
}