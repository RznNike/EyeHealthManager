package ru.rznnike.eyehealthmanager.domain.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

val decimal2Format = DecimalFormatSymbols().let {
    it.groupingSeparator = ' '
    it.decimalSeparator = '.'
    DecimalFormat("0.##", it).apply {
        groupingSize = 3
        isGroupingUsed = true
    }
}

fun String.toFloatOrNullSmart() =
    replace(" ", "")
    .replace(",", ".")
    .toFloatOrNull()