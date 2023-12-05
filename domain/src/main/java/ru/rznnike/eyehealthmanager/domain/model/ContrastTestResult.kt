package ru.rznnike.eyehealthmanager.domain.model

import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.toDate
import ru.rznnike.eyehealthmanager.domain.utils.toTimeStamp
import java.text.ParseException

class ContrastTestResult(
    id: Long = 0,
    timestamp: Long,
    val recognizedContrast: Int
) : TestResult(id, timestamp) {
    override fun exportToString(): String {
        return "%s\t%d".format(
            timestamp.toDate(GlobalConstants.DATE_PATTERN_FULL),
            recognizedContrast
        )
    }

    override fun contentEquals(other: TestResult?): Boolean {
        return (other is ContrastTestResult)
                && (this.timestamp == other.timestamp)
                && (this.recognizedContrast == other.recognizedContrast)
    }

    companion object {
        const val EXPORT_HEADER = "timestamp\trecognizedContrast"

        fun importFromString(string: String): ContrastTestResult? {
            val stringParts = string.split("\t")
            return if (stringParts.size < 2) {
                null
            } else {
                val timestamp = try {
                    stringParts[0].toTimeStamp(GlobalConstants.DATE_PATTERN_FULL)
                } catch (e: ParseException) {
                    null
                }
                val recognizedContrast = stringParts[1].toIntOrNull()
                if ((timestamp == null)
                    || (recognizedContrast == null)
                ) {
                    null
                } else {
                    ContrastTestResult(
                        timestamp = timestamp,
                        recognizedContrast = recognizedContrast
                    )
                }
            }
        }
    }
}