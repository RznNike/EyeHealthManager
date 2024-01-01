package ru.rznnike.eyehealthmanager.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ColorPerceptionTestResultTest {
    private val testResult1 = ColorPerceptionTestResult(
        id = 1,
        timestamp = 1704029085000,
        recognizedColorsCount = 35,
        allColorsCount = 39
    )

    @Test
    fun exportToString_data_success() {
        val expectedResult = "31.12.2023 16:24:45\t35\t39"

        val exportString = testResult1.exportToString()

        assertEquals(expectedResult, exportString)
    }

    @Test
    fun importFromString_correctData_success() {
        val string = "31.12.2023 16:24:45\t35\t39"

        val testResult = ColorPerceptionTestResult.importFromString(string)

        assert(testResult1.contentEquals(testResult))
    }

    @Test
    fun importFromString_corruptedData_null() {
        val string = "qwerty"

        val testResult = ColorPerceptionTestResult.importFromString(string)

        assertNull(testResult)
    }

    @Test
    fun contentEquals_sameData_true() {
        val otherTest = ColorPerceptionTestResult.importFromString(testResult1.exportToString())

        assert(testResult1.contentEquals(otherTest))
    }

    @Test
    fun contentEquals_differentData_false() {
        val string = "31.12.2023 16:24:45\t36\t39"

        val otherTest = ColorPerceptionTestResult.importFromString(string)

        assert(!testResult1.contentEquals(otherTest))
    }
}