package ru.rznnike.eyehealthmanager.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import ru.rznnike.eyehealthmanager.domain.model.enums.DaltonismAnomalyType

class DaltonismTestResultTest {
    private val testResult1 = DaltonismTestResult(
        id = 1,
        timestamp = 1704029085000,
        errorsCount = 5,
        anomalyType = DaltonismAnomalyType.DEITERANOMALY_A
    )

    @Test
    fun exportToString_data_success() {
        val expectedResult = "31.12.2023 16:24:45\t5\tDEITERANOMALY_A"

        val exportString = testResult1.exportToString()

        assertEquals(expectedResult, exportString)
    }

    @Test
    fun importFromString_correctData_success() {
        val string = "31.12.2023 16:24:45\t5\tDEITERANOMALY_A"

        val testResult = DaltonismTestResult.importFromString(string)

        assert(testResult1.contentEquals(testResult))
    }

    @Test
    fun importFromString_corruptedData_null() {
        val string = "qwerty"

        val testResult = DaltonismTestResult.importFromString(string)

        assertNull(testResult)
    }

    @Test
    fun contentEquals_sameData_true() {
        val otherTest = DaltonismTestResult.importFromString(testResult1.exportToString())

        assert(testResult1.contentEquals(otherTest))
    }

    @Test
    fun contentEquals_differentData_false() {
        val string = "31.12.2023 16:24:45\t6\tDEITERANOMALY_A"

        val otherTest = DaltonismTestResult.importFromString(string)

        assert(!testResult1.contentEquals(otherTest))
    }
}