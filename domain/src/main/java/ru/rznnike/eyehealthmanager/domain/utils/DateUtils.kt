package ru.rznnike.eyehealthmanager.domain.utils

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalQueries
import java.util.Calendar
import java.util.Locale

fun Long.toDate(pattern: String = GlobalConstants.DATE_PATTERN_SIMPLE): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(this)

fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this)
    .atZone(ZoneId.systemDefault())
    .toLocalDate()

fun LocalDate.toTimestamp(): Long =
    atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000L

fun Long.toYear() =
    SimpleDateFormat(GlobalConstants.DATE_PATTERN_YEAR, Locale.getDefault()).format(this).toInt()

fun Long.toMonth() =
    SimpleDateFormat(GlobalConstants.DATE_PATTERN_MONTH, Locale.getDefault()).format(this).toInt()

fun String.toTimeStamp(pattern: String = GlobalConstants.DATE_PATTERN_SIMPLE_WITH_TIME) =
    if (this.isNotEmpty()) {
        try {
            DateTimeFormatter.ofPattern(pattern).parse(this, ParsePosition(0)).let {
                val date = it.query(TemporalQueries.localDate())
                val time = it.query(TemporalQueries.localTime())
                LocalDateTime.of(date, time ?: LocalTime.MIN)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            }
        } catch (exception: Exception) {
            SimpleDateFormat(pattern, Locale.getDefault()).parse(this)?.time ?: 0
        }
    } else 0

fun Long.toCalendar(): Calendar = Calendar.getInstance().apply {
    timeInMillis = this@toCalendar
}

fun Long.getDayTime(): Long = toCalendar().run {
    (get(Calendar.HOUR_OF_DAY) * 60 * 60 + get(Calendar.MINUTE) * 60 + get(Calendar.SECOND)) * 1000L
}

fun getTodayCalendar(): Calendar = Calendar.getInstance().atStartOfDay()

fun Calendar.atStartOfDay() = apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

fun Calendar.atEndOfDay() = apply {
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    set(Calendar.MILLISECOND, 999)
}

fun Calendar.atStartOfMonth() = apply {
    set(Calendar.DAY_OF_MONTH, 1)
}.atStartOfDay()

fun Calendar.atEndOfMonth() = atStartOfMonth().apply {
    add(Calendar.MONTH, 1)
    add(Calendar.DAY_OF_MONTH, -1)
}
