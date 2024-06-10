package ru.rznnike.eyehealthmanager.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.common.DayPart
import ru.rznnike.eyehealthmanager.domain.model.test.TestEyesType
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestResult
import java.util.TimeZone

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

    @BeforeEach
    fun beforeEach() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Test
    fun exportToString_bothEyes_success() {
        val expectedResult = "31.12.2023 13:24:45\tLETTERS_RU\tBOTH\tEND\t0.55\t0.77\ttrue"

        val exportString = testResult1.exportToString()

        assertEquals(expectedResult, exportString)
    }

    @Test
    fun exportToString_oneEye_success() {
        val expectedResult = "31.12.2023 13:24:45\tSQUARE\tLEFT\tBEGINNING\t0.56\t-\tfalse"

        val exportString = testResult2.exportToString()

        assertEquals(expectedResult, exportString)
    }

    @Test
    fun importFromString_correctData_success() {
        val string = "31.12.2023 13:24:45\tLETTERS_RU\tBOTH\tEND\t0.55\t0.77\ttrue"

        val testResult = AcuityTestResult.importFromString(string)

        assertTrue(testResult1.contentEquals(testResult))
    }

    @Test
    fun importFromString_withCommaSeparator_success() {
        val string = "31.12.2023 13:24:45\tLETTERS_RU\tBOTH\tEND\t0,55\t0,77\ttrue"

        val testResult = AcuityTestResult.importFromString(string)

        assertTrue(testResult1.contentEquals(testResult))
    }

    @Test
    fun importFromString_badData_null() {
        val string = "qwerty"

        val testResult = AcuityTestResult.importFromString(string)

        assertNull(testResult)
    }

    @Test
    fun contentEquals_sameData_true() {
        val otherTest = AcuityTestResult.importFromString(testResult1.exportToString())

        assertTrue(testResult1.contentEquals(otherTest))
    }

    @Test
    fun contentEquals_differentData_false() {
        val otherTest = AcuityTestResult.importFromString(testResult2.exportToString())

        assertFalse(testResult1.contentEquals(otherTest))
    }
}