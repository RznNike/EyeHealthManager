package ru.rznnike.eyehealthmanager.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.rznnike.eyehealthmanager.domain.model.enums.NearFarAnswerType
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.toDate
import ru.rznnike.eyehealthmanager.domain.utils.toTimeStamp
import java.text.ParseException

@Parcelize
class NearFarTestResult(
    override var id: Long = 0,
    override var timestamp: Long = 0,
    val resultLeftEye: NearFarAnswerType,
    val resultRightEye: NearFarAnswerType
) : TestResult(id, timestamp), Parcelable {
    override fun exportToString() =
        "%s\t%s\t%s".format(
            timestamp.toDate(GlobalConstants.DATE_PATTERN_FULL),
            resultLeftEye.toString(),
            resultRightEye.toString()
        )

    override fun contentEquals(other: TestResult?) =
        (other is NearFarTestResult)
                && (timestamp == other.timestamp)
                && (resultLeftEye == other.resultLeftEye)
                && (resultRightEye == other.resultRightEye)

    companion object {
        const val EXPORT_HEADER = "timestamp\tresultLeftEye\tresultRightEye"

        fun importFromString(string: String): NearFarTestResult? {
            val stringParts = string.split("\t")
            return if (stringParts.size < 3) {
                null
            } else {
                try {
                    val timestamp = stringParts[0].toTimeStamp(GlobalConstants.DATE_PATTERN_FULL)
                    val resultLeftEye = NearFarAnswerType[stringParts[1]]
                    val resultRightEye = NearFarAnswerType[stringParts[2]]
                    if ((resultLeftEye == null) || (resultRightEye == null)) {
                        null
                    } else {
                        NearFarTestResult(
                            timestamp = timestamp,
                            resultLeftEye = resultLeftEye,
                            resultRightEye = resultRightEye
                        )
                    }
                } catch (e: ParseException) {
                    null
                }
            }
        }
    }
}