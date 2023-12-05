package ru.rznnike.eyehealthmanager.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.enums.DayPart
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.toDate
import ru.rznnike.eyehealthmanager.domain.utils.toTimeStamp
import java.text.ParseException

@Parcelize
class AcuityTestResult(
    override var id: Long = 0,
    override var timestamp: Long = 0,
    var symbolsType: AcuityTestSymbolsType = AcuityTestSymbolsType.LETTERS_RU,
    var testEyesType: TestEyesType = TestEyesType.BOTH,
    val dayPart: DayPart = DayPart.MIDDLE,
    var resultLeftEye: Int? = null,
    var resultRightEye: Int? = null,
    val measuredByDoctor: Boolean = false
) : TestResult(id, timestamp), Parcelable {
    override fun exportToString(): String {
        return "%s\t%s\t%s\t%s\t%.2f\t%.2f\t%s".format(
            timestamp.toDate(GlobalConstants.DATE_PATTERN_FULL),
            symbolsType.toString(),
            testEyesType.toString(),
            dayPart.toString(),
            if (resultLeftEye == null) null else ((resultLeftEye ?: 0) / 100f),
            if (resultRightEye == null) null else ((resultRightEye ?: 0) / 100f),
            measuredByDoctor.toString()
        )
    }

    override fun contentEquals(other: TestResult?): Boolean {
        return (other is AcuityTestResult)
                && (this.timestamp == other.timestamp)
                && (this.symbolsType == other.symbolsType)
                && (this.testEyesType == other.testEyesType)
                && (this.dayPart == other.dayPart)
                && (this.resultLeftEye == other.resultLeftEye)
                && (this.resultRightEye == other.resultRightEye)
                && (this.measuredByDoctor == other.measuredByDoctor)
    }

    companion object {
        const val EXPORT_HEADER = "timestamp\tsymbolsType\ttestEyesType\tdayPart\tresultLeftEye\tresultRightEye\tmeasuredByDoctor"

        fun importFromString(string: String): AcuityTestResult? {
            val stringParts = string.split("\t")
            return if (stringParts.size < 7) {
                null
            } else {
                val timestamp = try {
                    stringParts[0].toTimeStamp(GlobalConstants.DATE_PATTERN_FULL)
                } catch (e: ParseException) {
                    null
                }
                val symbolsType = AcuityTestSymbolsType.parseName(stringParts[1])
                val testEyesType = TestEyesType.parseName(stringParts[2])
                val dayPart = DayPart.parseName(stringParts[3])
                val resultLeftEye = stringParts[4].toFloatOrNull()?.let { (it * 100).toInt() }
                val resultRightEye = stringParts[5].toFloatOrNull()?.let { (it * 100).toInt() }
                val measuredByDoctor = stringParts[6].toBoolean()
                if ((timestamp == null)
                    || (symbolsType == null)
                    || (testEyesType == null)
                    || (dayPart == null)
                ) {
                    null
                } else {
                    AcuityTestResult(
                        timestamp = timestamp,
                        symbolsType = symbolsType,
                        testEyesType = testEyesType,
                        dayPart = dayPart,
                        resultLeftEye = resultLeftEye,
                        resultRightEye = resultRightEye,
                        measuredByDoctor = measuredByDoctor
                    )
                }
            }
        }
    }
}