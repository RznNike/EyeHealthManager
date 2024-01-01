package ru.rznnike.eyehealthmanager.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ContrastTestResultTest {
    private val testResult1 = ContrastTestResult(
        id = 1,
        timestamp = 1704029085000,
        recognizedContrast = 10
    )

    @Test
    fun exportToString_data_success() {
        val expectedResult = "31.12.2023 16:24:45\t10"

        val exportString = testResult1.exportToString()

        assertEquals(expectedResult, exportString)
    }

    @Test
    fun importFromString_correctData_success() {
        val string = "31.12.2023 16:24:45\t10"

        val testResult = ContrastTestResult.importFromString(string)

        assert(testResult1.contentEquals(testResult))
    }

    @Test
    fun importFromString_corruptedData_null() {
        val string = "qwerty"

        val testResult = ContrastTestResult.importFromString(string)

        assertNull(testResult)
    }

    @Test
    fun contentEquals_sameData_true() {
        val otherTest = ContrastTestResult.importFromString(testResult1.exportToString())

        assert(testResult1.contentEquals(otherTest))
    }

    @Test
    fun contentEquals_differentData_false() {
        val string = "31.12.2023 16:24:45\t20"

        val otherTest = ContrastTestResult.importFromString(string)

        assert(!testResult1.contentEquals(otherTest))
    }
}