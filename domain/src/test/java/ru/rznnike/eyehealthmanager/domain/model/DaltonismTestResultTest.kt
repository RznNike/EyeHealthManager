package ru.rznnike.eyehealthmanager.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.rznnike.eyehealthmanager.domain.model.test.daltonism.DaltonismAnomalyType
import ru.rznnike.eyehealthmanager.domain.model.test.daltonism.DaltonismTestResult
import java.util.TimeZone

class DaltonismTestResultTest {
    private val testResult1 = DaltonismTestResult(
        id = 1,
        timestamp = 1704029085000,
        errorsCount = 5,
        anomalyType = DaltonismAnomalyType.DEITERANOMALY_A
    )

    @BeforeEach
    fun beforeEach() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Test
    fun exportToString_data_success() {
        val expectedResult = "31.12.2023 13:24:45\t5\tDEITERANOMALY_A"

        val exportString = testResult1.exportToString()

        assertEquals(expectedResult, exportString)
    }

    @Test
    fun importFromString_correctData_success() {
        val string = "31.12.2023 13:24:45\t5\tDEITERANOMALY_A"

        val testResult = DaltonismTestResult.importFromString(string)

        assertTrue(testResult1.contentEquals(testResult))
    }

    @Test
    fun importFromString_badData_null() {
        val string = "qwerty"

        val testResult = DaltonismTestResult.importFromString(string)

        assertNull(testResult)
    }

    @Test
    fun contentEquals_sameData_true() {
        val otherTest = DaltonismTestResult.importFromString(testResult1.exportToString())

        assertTrue(testResult1.contentEquals(otherTest))
    }

    @Test
    fun contentEquals_differentData_false() {
        val string = "31.12.2023 13:24:45\t6\tDEITERANOMALY_A"

        val otherTest = DaltonismTestResult.importFromString(string)

        assertFalse(testResult1.contentEquals(otherTest))
    }
}