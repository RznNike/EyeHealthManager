package ru.rznnike.eyehealthmanager.domain.model

import ru.rznnike.eyehealthmanager.domain.model.enums.NearFarAnswerType
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.toDate
import ru.rznnike.eyehealthmanager.domain.utils.toTimeStamp
import java.text.ParseException

class NearFarTestResult(
    id: Long = 0,
    timestamp: Long,
    val resultLeftEye: NearFarAnswerType? = null,
    val resultRightEye: NearFarAnswerType? = null
) : TestResult(id, timestamp) {
    override fun exportToString(): String {
        return "%s\t%s\t%s".format(
            timestamp.toDate(GlobalConstants.DATE_PATTERN_FULL),
            resultLeftEye.toString(),
            resultRightEye.toString()
        )
    }

    override fun contentEquals(other: TestResult?): Boolean {
        return (other is NearFarTestResult)
                && (this.timestamp == other.timestamp)
                && (this.resultLeftEye == other.resultLeftEye)
                && (this.resultRightEye == other.resultRightEye)
    }

    companion object {
        const val EXPORT_HEADER = "timestamp\tresultLeftEye\tresultRightEye"

        fun importFromString(string: String): NearFarTestResult? {
            val stringParts = string.split("\t")
            return if (stringParts.size < 3) {
                null
            } else {
                val timestamp = try {
                    stringParts[0].toTimeStamp(GlobalConstants.DATE_PATTERN_FULL)
                } catch (e: ParseException) {
                    null
                }
                val resultLeftEye = NearFarAnswerType.parseName(stringParts[1])
                val resultRightEye = NearFarAnswerType.parseName(stringParts[2])
                if ((timestamp == null)
                    || (resultLeftEye == null)
                    || (resultRightEye == null)
                ) {
                    null
                } else {
                    NearFarTestResult(
                        timestamp = timestamp,
                        resultLeftEye = resultLeftEye,
                        resultRightEye = resultRightEye
                    )
                }
            }
        }
    }
}