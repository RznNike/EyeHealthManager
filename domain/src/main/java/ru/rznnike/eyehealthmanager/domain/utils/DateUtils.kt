package ru.rznnike.eyehealthmanager.domain.utils

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalQueries
import java.util.Locale
import java.util.TimeZone

fun Long.toDate(
    pattern: String = GlobalConstants.DATE_PATTERN_SIMPLE,
    zeroTimeZone: Boolean = false
): String {
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    if (zeroTimeZone) {
        format.timeZone = TimeZone.getTimeZone("UTC")
    }
    return format.format(this)
}

fun String.toTimeStamp(pattern: String = GlobalConstants.DATE_PATTERN_SIMPLE_WITH_TIME) =
    if (this.isNotEmpty()) {
        try {
            DateTimeFormatter.ofPattern(pattern).parse(this, ParsePosition(0)).let {
                val date = it.query(TemporalQueries.localDate())
                val time = it.query(TemporalQueries.localTime())
                ZonedDateTime.of(date, time ?: LocalTime.MIN, ZoneId.systemDefault()).millis()
            }
        } catch (exception: Exception) {
            SimpleDateFormat(pattern, Locale.getDefault()).parse(this)?.time ?: 0
        }
    } else 0

fun Long.toDateTime(): ZonedDateTime = Instant.ofEpochMilli(this)
    .atZone(ZoneId.systemDefault())

fun Long.toLocalDate(): LocalDate = toDateTime().toLocalDate()

fun Long.getDayTime() = toDateTime().toLocalTime().toNanoOfDay() / 1000_000L

fun ZonedDateTime.millis() = toInstant().toEpochMilli()

fun LocalDateTime.millis() = atZone(ZoneId.systemDefault()).millis()

fun LocalDate.atEndOfDay(): LocalDateTime = atTime(23, 59, 59, 999_999_999)

fun currentTimeMillis() = Clock.systemUTC().millis()
