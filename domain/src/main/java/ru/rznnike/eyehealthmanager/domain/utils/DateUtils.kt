package ru.rznnike.eyehealthmanager.domain.utils

import android.content.Context
import ru.rznnike.eyehealthmanager.domain.R
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

fun Long.toDate(pattern: String = GlobalConstants.DATE_PATTERN_SIMPLE): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(this)

fun Long.toSmartDate(
    context: Context,
    pattern: String = GlobalConstants.DATE_PATTERN_SIMPLE
): String {
    return when (ChronoUnit.DAYS.between(System.currentTimeMillis().toLocalDate(), this.toLocalDate())) {
        0L -> context.getString(R.string.today)
        -1L -> context.getString(R.string.yesterday)
        else -> SimpleDateFormat(pattern, Locale.getDefault()).format(this)
    }
}

fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this)
    .atZone(ZoneId.systemDefault())
    .toLocalDate()

fun LocalDate.toTimestamp(): Long =
    atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000L

fun Long.toYear() =
    SimpleDateFormat(GlobalConstants.DATE_PATTERN_YEAR, Locale.getDefault()).format(this).toInt()

fun Long.toMonth() =
    SimpleDateFormat(GlobalConstants.DATE_PATTERN_MONTH, Locale.getDefault()).format(this).toInt()

fun String.toTimeStamp(pattern: String = GlobalConstants.DATE_PATTERN_SIMPLE_WITH_TIME): Long {
    return if (this.isNotEmpty()) {
        SimpleDateFormat(pattern, Locale.getDefault()).parse(this)?.time ?: 0
    } else 0
}

fun getTodayCalendar(): Calendar = Calendar.getInstance().apply {
    timeInMillis = System.currentTimeMillis().toLocalDate().toTimestamp()
}
