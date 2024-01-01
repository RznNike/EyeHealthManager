package ru.rznnike.eyehealthmanager.domain.model

import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.enums.AstigmatismAnswerType
import ru.rznnike.eyehealthmanager.domain.model.enums.DayPart
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType

class AstigmatismTestResultTest {
    private val testResult1 = AstigmatismTestResult(
        id = 1,
        timestamp = 1704029085000,
        resultLeftEye = AstigmatismAnswerType.OK,
        resultRightEye = AstigmatismAnswerType.ANOMALY
    )

    @Test
    fun exportToString_data_success() {
        val expectedResult = "31.12.2023 16:24:45\tOK\tANOMALY"

        val exportString = testResult1.exportToString()

        assertEquals(expectedResult, exportString)
    }

    @Test
    fun importFromString_correctData_success() {
        val string = "31.12.2023 16:24:45\tOK\tANOMALY"

        val testResult = AstigmatismTestResult.importFromString(string)

        assert(testResult1.contentEquals(testResult))
    }

    @Test
    fun importFromString_corruptedData_null() {
        val string = "qwerty"

        val testResult = AstigmatismTestResult.importFromString(string)

        assertNull(testResult)
    }

    @Test
    fun contentEquals_sameData_true() {
        val otherTest = AstigmatismTestResult.importFromString(testResult1.exportToString())

        assert(testResult1.contentEquals(otherTest))
    }

    @Test
    fun contentEquals_differentData_false() {
        val string = "31.12.2023 16:24:45\tOK\tOK"

        val otherTest = AstigmatismTestResult.importFromString(string)

        assert(!testResult1.contentEquals(otherTest))
    }
}