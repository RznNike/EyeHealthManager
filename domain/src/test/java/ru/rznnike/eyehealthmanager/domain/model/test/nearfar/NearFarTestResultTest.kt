package ru.rznnike.eyehealthmanager.domain.model.test.nearfar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.TimeZone

class NearFarTestResultTest {
    private val testResult1 = NearFarTestResult(
        id = 1,
        timestamp = 1704029085000,
        resultLeftEye = NearFarAnswerType.EQUAL,
        resultRightEye = NearFarAnswerType.EQUAL
    )

    @BeforeEach
    fun beforeEach() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Test
    fun exportToString_data_success() {
        val expectedResult = "31.12.2023 13:24:45\tEQUAL\tEQUAL"

        val exportString = testResult1.exportToString()

        assertEquals(expectedResult, exportString)
    }

    @Test
    fun importFromString_correctData_success() {
        val string = "31.12.2023 13:24:45\tEQUAL\tEQUAL"

        val testResult = NearFarTestResult.importFromString(string)

        assertTrue(testResult1.contentEquals(testResult))
    }

    @Test
    fun importFromString_badData_null() {
        val string = "qwerty"

        val testResult = NearFarTestResult.importFromString(string)

        assertNull(testResult)
    }

    @Test
    fun contentEquals_sameData_true() {
        val otherTest = NearFarTestResult.importFromString(testResult1.exportToString())

        assertTrue(testResult1.contentEquals(otherTest))
    }

    @Test
    fun contentEquals_differentData_false() {
        val string = "31.12.2023 13:24:45\tEQUAL\tRED_BETTER"

        val otherTest = NearFarTestResult.importFromString(string)

        assertFalse(testResult1.contentEquals(otherTest))
    }
}