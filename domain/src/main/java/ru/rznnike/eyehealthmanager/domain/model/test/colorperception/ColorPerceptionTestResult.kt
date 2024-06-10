package ru.rznnike.eyehealthmanager.domain.model.test.colorperception

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.rznnike.eyehealthmanager.domain.model.test.TestResult
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.toDate
import ru.rznnike.eyehealthmanager.domain.utils.toTimeStamp
import java.text.ParseException

@Parcelize
class ColorPerceptionTestResult(
    override var id: Long = 0,
    override var timestamp: Long = 0,
    val recognizedColorsCount: Int,
    val allColorsCount: Int
) : TestResult(id, timestamp), Parcelable {
    override fun exportToString() =
        "%s\t%d\t%d".format(
            timestamp.toDate(GlobalConstants.DATE_PATTERN_FULL),
            recognizedColorsCount,
            allColorsCount
        )

    override fun contentEquals(other: TestResult?) =
        (other is ColorPerceptionTestResult)
                && (timestamp == other.timestamp)
                && (recognizedColorsCount == other.recognizedColorsCount)
                && (allColorsCount == other.allColorsCount)

    companion object {
        const val EXPORT_HEADER = "timestamp\trecognizedColorsCount\tallColorsCount"

        fun importFromString(string: String): ColorPerceptionTestResult? {
            val stringParts = string.split("\t")
            return if (stringParts.size < 3) {
                null
            } else {
                val timestamp = try {
                    stringParts[0].toTimeStamp(GlobalConstants.DATE_PATTERN_FULL)
                } catch (e: ParseException) {
                    null
                }
                val recognizedColorsCount = stringParts[1].toIntOrNull()
                val allColorsCount = stringParts[2].toIntOrNull()
                if ((timestamp == null) || (recognizedColorsCount == null) || (allColorsCount == null)) {
                    null
                } else {
                    ColorPerceptionTestResult(
                        timestamp = timestamp,
                        recognizedColorsCount = recognizedColorsCount,
                        allColorsCount = allColorsCount
                    )
                }
            }
        }
    }
}