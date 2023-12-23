package ru.rznnike.eyehealthmanager.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.toDate
import ru.rznnike.eyehealthmanager.domain.utils.toTimeStamp
import java.text.ParseException

@Parcelize
class ContrastTestResult(
    override var id: Long = 0,
    override var timestamp: Long = 0,
    val recognizedContrast: Int
) : TestResult(id, timestamp), Parcelable {
    override fun exportToString() =
        "%s\t%d".format(
            timestamp.toDate(GlobalConstants.DATE_PATTERN_FULL),
            recognizedContrast
        )

    override fun contentEquals(other: TestResult?) =
        (other is ContrastTestResult)
                && (timestamp == other.timestamp)
                && (recognizedContrast == other.recognizedContrast)

    companion object {
        const val EXPORT_HEADER = "timestamp\trecognizedContrast"

        fun importFromString(string: String): ContrastTestResult? {
            val stringParts = string.split("\t")
            return if (stringParts.size < 2) {
                null
            } else {
                try {
                    val timestamp = stringParts[0].toTimeStamp(GlobalConstants.DATE_PATTERN_FULL)
                    val recognizedContrast = stringParts[1].toIntOrNull()
                    recognizedContrast?.let {
                        ContrastTestResult(
                            timestamp = timestamp,
                            recognizedContrast = recognizedContrast
                        )
                    }
                } catch (e: ParseException) {
                    null
                }
            }
        }
    }
}