package ru.rznnike.eyehealthmanager.domain.model.test.colorperception

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.TimeZone

class ColorPerceptionTestResultTest {
    private val testResult1 = ColorPerceptionTestResult(
        id = 1,
        timestamp = 1704029085000,
        recognizedColorsCount = 35,
        allColorsCount = 39
    )

    @BeforeEach
    fun beforeEach() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Test
    fun exportToString_data_success() {
        val expectedResult = "31.12.2023 13:24:45\t35\t39"

        val exportString = testResult1.exportToString()

        assertEquals(expectedResult, exportString)
    }

    @Test
    fun importFromString_correctData_success() {
        val string = "31.12.2023 13:24:45\t35\t39"

        val testResult = ColorPerceptionTestResult.importFromString(string)

        assertTrue(testResult1.contentEquals(testResult))
    }

    @Test
    fun importFromString_badData_null() {
        val string = "qwerty"

        val testResult = ColorPerceptionTestResult.importFromString(string)

        assertNull(testResult)
    }

    @Test
    fun contentEquals_sameData_true() {
        val otherTest = ColorPerceptionTestResult.importFromString(testResult1.exportToString())

        assertTrue(testResult1.contentEquals(otherTest))
    }

    @Test
    fun contentEquals_differentData_false() {
        val string = "31.12.2023 13:24:45\t36\t39"

        val otherTest = ColorPerceptionTestResult.importFromString(string)

        assertFalse(testResult1.contentEquals(otherTest))
    }
}