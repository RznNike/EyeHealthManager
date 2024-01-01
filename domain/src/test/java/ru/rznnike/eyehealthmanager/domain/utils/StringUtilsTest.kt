package ru.rznnike.eyehealthmanager.domain.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class StringUtilsTest {
    @Test
    fun decimal2Format_manyDecimalSymbols_twoSymbolsLeft() {
        val number = 123.45123
        val expectedString = "123.45"

        val result = decimal2Format.format(number)

        assertEquals(expectedString, result)
    }

    @Test
    fun decimal2Format_bigTruncatedPart_roundUp() {
        val number = 123.45678
        val expectedString = "123.46"

        val result = decimal2Format.format(number)

        assertEquals(expectedString, result)
    }

    @Test
    fun decimal2Format_leadingZero_zeroShown() {
        val number = 0.45678
        val expectedString = "0.46"

        val result = decimal2Format.format(number)

        assertEquals(expectedString, result)
    }

    @Test
    fun decimal2Format_oneDecimalSymbol_oneShown() {
        val number = 123.4
        val expectedString = "123.4"

        val result = decimal2Format.format(number)

        assertEquals(expectedString, result)
    }

    @Test
    fun decimal2Format_noDecimalSymbols_noDot() {
        val number = 123
        val expectedString = "123"

        val result = decimal2Format.format(number)

        assertEquals(expectedString, result)
    }

    @Test
    fun decimal2Format_bigNumber_groupingAdded() {
        val number = 123456789.123
        val expectedString = "123 456 789.12"

        val result = decimal2Format.format(number)

        assertEquals(expectedString, result)
    }

    @Test
    fun stringToFloatOrNullSmart_goodString_success() {
        val string = "123.45"
        val expectedNumber = 123.45f

        val result = string.toFloatOrNullSmart()

        assertEquals(expectedNumber, result)
    }

    @Test
    fun stringToFloatOrNullSmart_commaSeparator_success() {
        val string = "123,45"
        val expectedNumber = 123.45f

        val result = string.toFloatOrNullSmart()

        assertEquals(expectedNumber, result)
    }

    @Test
    fun stringToFloatOrNullSmart_integer_success() {
        val string = "123"
        val expectedNumber = 123f

        val result = string.toFloatOrNullSmart()

        assertEquals(expectedNumber, result)
    }

    @Test
    fun stringToFloatOrNullSmart_emptyString_null() {
        val string = ""

        val result = string.toFloatOrNullSmart()

        assertNull(result)
    }

    @Test
    fun stringToFloatOrNullSmart_badString_null() {
        val string = "qwerty"

        val result = string.toFloatOrNullSmart()

        assertNull(result)
    }

    @Test
    fun stringToFloatOrNullSmart_withSeparators_success() {
        val string = "123 456.12"
        val expectedNumber = 123456.12f

        val result = string.toFloatOrNullSmart()

        assertEquals(expectedNumber, result)
    }
}