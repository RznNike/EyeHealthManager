package ru.rznnike.eyehealthmanager.domain.utils

import java.math.BigDecimal

fun String.toBigDecimalOrNullSmart() =
    replace(" ", "")
    .replace(",", ".")
    .toBigDecimalOrNull()

fun String.toBigDecimalSmart() = toBigDecimalOrNullSmart() ?: BigDecimal.ZERO

fun String.toFloatOrNullSmart() =
    replace(" ", "")
    .replace(",", ".")
    .toDoubleOrNull()

fun String.toDoubleOrNullSmart() =
    replace(" ", "")
    .replace(",", ".")
    .toDoubleOrNull()
