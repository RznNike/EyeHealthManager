package ru.rznnike.eyehealthmanager.domain.utils

import android.graphics.Color

object GlobalConstants{
    const val DATE_PATTERN_FULL = "dd.MM.yyyy HH:mm:ss"
    const val DATE_PATTERN_FULL_FOR_PATH = "dd.MM.yyyy HH-mm-ss"
    const val DATE_PATTERN_SIMPLE = "dd.MM.yyyy"
    const val DATE_PATTERN_SIMPLE_WITH_TIME = "dd.MM.yyyy HH:mm"
    const val DATE_PATTERN_YEAR = "yyyy"
    const val DATE_PATTERN_MONTH = "MM"
    const val DATE_PATTERN_CLOCK = "HH:mm"

    const val APP_EXIT_DURATION_MS = 2500L
    const val PRELOAD_ITEM_POSITION = 10

    const val EXPORT_PAGE_SIZE = 100
    const val IMPORT_PAGE_SIZE = 100
    const val APP_DIR = "Eye Health Manager"
    const val EXPORT_DIR = "export"

    val COLOR_PERCEPTION_TEST_COLORS: Array<Int> = arrayOf(
        Color.rgb(155, 92, 203),
        Color.rgb(165, 95, 217),
        Color.rgb(172, 99, 230),
        Color.rgb(202, 113, 246),
        Color.rgb(247, 131, 254),
        Color.rgb(150, 113, 246),
        Color.rgb(118, 113, 246),
        Color.rgb(113, 118, 246),
        Color.rgb(113, 125, 246),
        Color.rgb(113, 133, 246),
        Color.rgb(113, 158, 246),
        Color.rgb(113, 173, 246),
        Color.rgb(113, 184, 246),
        Color.rgb(113, 195, 246),
        Color.rgb(113, 210, 246),
        Color.rgb(113, 221, 246),
        Color.rgb(113, 239, 246),
        Color.rgb(113, 246, 243),
        Color.rgb(113, 246, 213),
        Color.rgb(113, 246, 188),
        Color.rgb(113, 246, 152),
        Color.rgb(113, 246, 135),
        Color.rgb(113, 246, 113),
        Color.rgb(146, 246, 113),
        Color.rgb(184, 246, 113),
        Color.rgb(208, 246, 113),
        Color.rgb(225, 255, 99),
        Color.rgb(255, 255, 85),
        Color.rgb(255, 250, 72),
        Color.rgb(246, 229, 112),
        Color.rgb(246, 219, 112),
        Color.rgb(246, 207, 112),
        Color.rgb(246, 182, 112),
        Color.rgb(246, 157, 112),
        Color.rgb(246, 141, 112),
        Color.rgb(246, 119, 112),
        Color.rgb(246, 112, 112),
        Color.rgb(215, 91, 91),
        Color.rgb(150, 73, 73)
    )
}