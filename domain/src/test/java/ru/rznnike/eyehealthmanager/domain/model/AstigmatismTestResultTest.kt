package ru.rznnike.eyehealthmanager.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.rznnike.eyehealthmanager.domain.model.enums.AstigmatismAnswerType
import java.util.TimeZone

class AstigmatismTestResultTest {
    private val testResult1 = AstigmatismTestResult(
        id = 1,
        timestamp = 1704029085000,
        resultLeftEye = AstigmatismAnswerType.OK,
        resultRightEye = AstigmatismAnswerType.ANOMALY
    )

    @BeforeEach
    fun beforeEach() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Test
    fun exportToString_data_success() {
        val expectedResult = "31.12.2023 13:24:45\tOK\tANOMALY"

        val exportString = testResult1.exportToString()

        assertEquals(expectedResult, exportString)
    }

    @Test
    fun importFromString_correctData_success() {
        val string = "31.12.2023 13:24:45\tOK\tANOMALY"

        val testResult = AstigmatismTestResult.importFromString(string)

        assert(testResult1.contentEquals(testResult))
    }

    @Test
    fun importFromString_badData_null() {
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
        val string = "31.12.2023 13:24:45\tOK\tOK"

        val otherTest = AstigmatismTestResult.importFromString(string)

        assert(!testResult1.contentEquals(otherTest))
    }
}