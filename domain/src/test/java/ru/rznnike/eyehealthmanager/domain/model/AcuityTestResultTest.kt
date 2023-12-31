package ru.rznnike.eyehealthmanager.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.enums.DayPart
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType
import java.text.DecimalFormatSymbols
import java.util.Locale

class AcuityTestResultTest {
    private val testResult1 = AcuityTestResult(
        id = 1,
        timestamp = 1704029085000,
        symbolsType = AcuityTestSymbolsType.LETTERS_RU,
        testEyesType = TestEyesType.BOTH,
        dayPart = DayPart.END,
        resultLeftEye = 55,
        resultRightEye = 77,
        measuredByDoctor = true
    )
    private val testResult2 = AcuityTestResult(
        id = 2,
        timestamp = 1704029085000,
        symbolsType = AcuityTestSymbolsType.SQUARE,
        testEyesType = TestEyesType.LEFT,
        dayPart = DayPart.BEGINNING,
        resultLeftEye = 56,
        resultRightEye = null,
        measuredByDoctor = false
    )

    @Test
    fun exportToString_bothEyes_success() {
        val separatorChar = DecimalFormatSymbols.getInstance().decimalSeparator.toString()
        val expectedResult = "31.12.2023 16:24:45\tLETTERS_RU\tBOTH\tEND\t0%s55\t0%s77\ttrue".format(
            Locale.getDefault(),
            separatorChar,
            separatorChar
        )

        val exportString = testResult1.exportToString()

        assertEquals(expectedResult, exportString)
    }

    @Test
    fun exportToString_oneEye_success() {
        val separatorChar = DecimalFormatSymbols.getInstance().decimalSeparator.toString()
        val expectedResult = "31.12.2023 16:24:45\tSQUARE\tLEFT\tBEGINNING\t0%s56\tnull\tfalse".format(
            Locale.getDefault(),
            separatorChar,
            separatorChar
        )

        val exportString = testResult2.exportToString()

        assertEquals(expectedResult, exportString)
    }

//    @Test
//    fun contentEquals() {
//    }
}