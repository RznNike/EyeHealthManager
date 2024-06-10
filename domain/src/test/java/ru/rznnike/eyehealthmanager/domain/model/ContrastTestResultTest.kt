package ru.rznnike.eyehealthmanager.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.rznnike.eyehealthmanager.domain.model.test.contrast.ContrastTestResult
import java.util.TimeZone

class ContrastTestResultTest {
    private val testResult1 = ContrastTestResult(
        id = 1,
        timestamp = 1704029085000,
        recognizedContrast = 10
    )

    @BeforeEach
    fun beforeEach() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Test
    fun exportToString_data_success() {
        val expectedResult = "31.12.2023 13:24:45\t10"

        val exportString = testResult1.exportToString()

        assertEquals(expectedResult, exportString)
    }

    @Test
    fun importFromString_correctData_success() {
        val string = "31.12.2023 13:24:45\t10"

        val testResult = ContrastTestResult.importFromString(string)

        assertTrue(testResult1.contentEquals(testResult))
    }

    @Test
    fun importFromString_badData_null() {
        val string = "qwerty"

        val testResult = ContrastTestResult.importFromString(string)

        assertNull(testResult)
    }

    @Test
    fun contentEquals_sameData_true() {
        val otherTest = ContrastTestResult.importFromString(testResult1.exportToString())

        assertTrue(testResult1.contentEquals(otherTest))
    }

    @Test
    fun contentEquals_differentData_false() {
        val string = "31.12.2023 13:24:45\t20"

        val otherTest = ContrastTestResult.importFromString(string)

        assertFalse(testResult1.contentEquals(otherTest))
    }
}