package ru.rznnike.eyehealthmanager.domain.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.text.ParseException
import java.util.Calendar
import java.util.TimeZone

class DateUtilsTest {
    @BeforeEach
    fun beforeEach() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Test
    fun longToDate_defaultPattern_success() {
        val timestamp = 1704124202000L
        val expectedString = "01.01.2024"

        val result = timestamp.toDate()

        assertEquals(expectedString, result)
    }

    @Test
    fun longToDate_customPattern_success() {
        val timestamp = 1704124202000L
        val expectedString = "01.01.2024 15:50"

        val result = timestamp.toDate(GlobalConstants.DATE_PATTERN_SIMPLE_WITH_TIME)

        assertEquals(expectedString, result)
    }

    @Test
    fun longToDate_badPattern_exception() {
        val timestamp = 1704124202000L

        assertThrows<IllegalArgumentException> {
            timestamp.toDate("qwerty")
        }
    }

    @Test
    fun stringToTimeStamp_defaultPattern_success() {
        val string = "01.01.2024 15:50"
        val expectedTimestamp = 1704124200000L

        val result = string.toTimeStamp()

        assertEquals(expectedTimestamp, result)
    }

    @Test
    fun stringToTimeStamp_customPattern_success() {
        val string = "01.01.2024 15:50"
        val expectedTimestamp = 1704067200000L

        val result = string.toTimeStamp(GlobalConstants.DATE_PATTERN_SIMPLE)

        assertEquals(expectedTimestamp, result)
    }

    @Test
    fun stringToTimeStamp_badPattern_exception() {
        val string = "01.01.2024 15:50"

        assertThrows<IllegalArgumentException> {
            string.toTimeStamp("qwerty")
        }
    }

    @Test
    fun stringToTimeStamp_emptyString_zero() {
        val string = ""
        val expectedTimestamp = 0L

        val result = string.toTimeStamp()

        assertEquals(expectedTimestamp, result)
    }

    @Test
    fun stringToTimeStamp_badString_exception() {
        val string = "qwerty"

        assertThrows<ParseException> {
            string.toTimeStamp()
        }
    }

    @Test
    fun longToCalendar_success() {
        val timestamp = 1704124202000L

        val result = timestamp.toCalendar()

        assertEquals(timestamp, result.timeInMillis)
    }

    @Test
    fun longGetDayTime_success() {
        val timestamp = 1704124202001L
        val expectedDayTime = 57002001L

        val result = timestamp.getDayTime()

        assertEquals(expectedDayTime, result)
    }

    @Test
    fun getTodayCalendar_success() {
        val expectedTimestamp = Calendar.getInstance()
            .apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            .timeInMillis

        val result = getTodayCalendar().timeInMillis

        assertEquals(expectedTimestamp, result)
    }

    @Test
    fun atStartOfDay_success() {
        val result = Calendar.getInstance().atStartOfDay()

        assertEquals(0, result.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, result.get(Calendar.MINUTE))
        assertEquals(0, result.get(Calendar.SECOND))
        assertEquals(0, result.get(Calendar.MILLISECOND))
    }

    @Test
    fun atEndOfDay_success() {
        val result = Calendar.getInstance().atEndOfDay()

        assertEquals(23, result.get(Calendar.HOUR_OF_DAY))
        assertEquals(59, result.get(Calendar.MINUTE))
        assertEquals(59, result.get(Calendar.SECOND))
        assertEquals(999, result.get(Calendar.MILLISECOND))
    }

    @Test
    fun atStartOfMonth_success() {
        val defaultCalendar = Calendar.getInstance()

        val result = Calendar.getInstance()
            .apply { timeInMillis = defaultCalendar.timeInMillis }
            .atStartOfMonth()

        assertEquals(defaultCalendar.get(Calendar.YEAR), result.get(Calendar.YEAR))
        assertEquals(defaultCalendar.get(Calendar.MONTH), result.get(Calendar.MONTH))
        assertEquals(1, result.get(Calendar.DAY_OF_MONTH))
        assertEquals(0, result.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, result.get(Calendar.MINUTE))
        assertEquals(0, result.get(Calendar.SECOND))
        assertEquals(0, result.get(Calendar.MILLISECOND))
    }

    @Test
    fun atEndOfMonth_success() {
        val defaultCalendar = Calendar.getInstance().apply { timeInMillis = 0 }

        val result = Calendar.getInstance()
            .apply { timeInMillis = defaultCalendar.timeInMillis }
            .atEndOfMonth()

        assertEquals(defaultCalendar.get(Calendar.YEAR), result.get(Calendar.YEAR))
        assertEquals(defaultCalendar.get(Calendar.MONTH), result.get(Calendar.MONTH))
        assertEquals(31, result.get(Calendar.DAY_OF_MONTH))
        assertEquals(0, result.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, result.get(Calendar.MINUTE))
        assertEquals(0, result.get(Calendar.SECOND))
        assertEquals(0, result.get(Calendar.MILLISECOND))
    }
}