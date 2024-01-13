package ru.rznnike.eyehealthmanager.domain.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.text.ParseException
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
    fun longToDateTime_utc_success() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        val timestamp = 1704124202000L

        val result = timestamp.toDateTime()

        assertEquals(2024, result.year)
        assertEquals(1, result.monthValue)
        assertEquals(1, result.dayOfMonth)
        assertEquals(15, result.hour)
        assertEquals(50, result.minute)
        assertEquals(2, result.second)
        assertEquals(0, result.nano)
    }

    @Test
    fun longToDateTime_timeZoneWithOffset_success() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+3"))
        val timestamp = 1704124202000L

        val result = timestamp.toDateTime()

        assertEquals(2024, result.year)
        assertEquals(1, result.monthValue)
        assertEquals(1, result.dayOfMonth)
        assertEquals(18, result.hour)
        assertEquals(50, result.minute)
        assertEquals(2, result.second)
        assertEquals(0, result.nano)
    }

    @Test
    fun longToLocalDate_success() {
        val timestamp = 1704124202000L

        val result = timestamp.toLocalDate()

        assertEquals(2024, result.year)
        assertEquals(1, result.monthValue)
        assertEquals(1, result.dayOfMonth)
    }

    @Test
    fun longGetDayTime_success() {
        val timestamp = 1704124202001L
        val expectedDayTime = 57002001L

        val result = timestamp.getDayTime()

        assertEquals(expectedDayTime, result)
    }

    @Test
    fun zonedDateTimeMillis_success() {
        val timestamp = 1704124202001L

        val result = timestamp.toDateTime().millis()

        assertEquals(timestamp, result)
    }

    @Test
    fun localDateTimeMillis_success() {
        val timestamp = 1704124202001L

        val result = timestamp.toDateTime().toLocalDateTime().millis()

        assertEquals(timestamp, result)
    }

    @Test
    fun localDateAtEndOfDay_success() {
        val timestamp = 1704124202000L
        val expectedResult = 1704153599999L

        val result = timestamp.toDateTime().toLocalDate().atEndOfDay().millis()

        assertEquals(expectedResult, result)
    }
}