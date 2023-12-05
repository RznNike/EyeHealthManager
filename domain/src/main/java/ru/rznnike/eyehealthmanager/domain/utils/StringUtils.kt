package ru.rznnike.eyehealthmanager.domain.utils

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

val decimal2Format = DecimalFormatSymbols().let {
    it.groupingSeparator = ' '
    it.decimalSeparator = '.'
    DecimalFormat("0.##", it)
}

fun String.toBigDecimalOrNullSmart() = this
    .replace(" ", "")
    .replace(",", ".")
    .toBigDecimalOrNull()

fun String.toBigDecimalSmart(): BigDecimal = this
    .toBigDecimalOrNullSmart()
    ?: BigDecimal.ZERO

fun String.toFloatOrNullSmart() = this
    .replace(" ", "")
    .replace(",", ".")
    .toDoubleOrNull()

fun String.toDoubleOrNullSmart() = this
    .replace(" ", "")
    .replace(",", ".")
    .toDoubleOrNull()
