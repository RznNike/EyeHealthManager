package ru.rznnike.eyehealthmanager.domain.utils

object GlobalConstants {
    const val DATE_PATTERN_FULL = "dd.MM.yyyy HH:mm:ss"
    const val DATE_PATTERN_FULL_FOR_PATH = "dd.MM.yyyy HH-mm-ss"
    const val DATE_PATTERN_SIMPLE = "dd.MM.yyyy"
    const val DATE_PATTERN_SIMPLE_WITH_TIME = "dd.MM.yyyy HH:mm"
    const val DATE_PATTERN_CLOCK = "HH:mm"

    const val APP_EXIT_DURATION_MS = 2500L
    const val PRELOAD_ITEM_POSITION = 10

    const val EXPORT_PAGE_SIZE = 100
    const val IMPORT_PAGE_SIZE = 100
    const val APP_DIR = "Eye Health Manager"
    const val EXPORT_DIR = "export"

    const val FEEDBACK_EMAIL_ADDRESS = "rznnike@yandex.ru"
    const val REPOSITORY_LINK = "https://github.com/RznNike/EyeHealthManager"

    const val MINUTE_MS = 60_1000L
    const val DAY_MS = 86_400_000L
    const val ANALYSIS_GROUPING_MIN_RANGE_DAYS = 3L
    const val ANALYSIS_GROUPING_MIN_RANGE_MS = ANALYSIS_GROUPING_MIN_RANGE_DAYS * DAY_MS
    const val ANALYSIS_GROUPING_MAX_RANGE_MS = 14 * DAY_MS
    const val ANALYSIS_GROUPING_MIN_SIZE = 5
    const val ANALYSIS_MIN_GROUPS_COUNT = 2
    const val ANALYSIS_MIN_RESULTS_COUNT = ANALYSIS_GROUPING_MIN_SIZE * ANALYSIS_MIN_GROUPS_COUNT
    const val ANALYSIS_MIN_RANGE_DAYS = ANALYSIS_GROUPING_MIN_RANGE_DAYS * ANALYSIS_MIN_GROUPS_COUNT
    const val ANALYSIS_MAX_RANGE_DAYS = 90L
    const val ANALYSIS_MAX_RANGE_MS = (ANALYSIS_MAX_RANGE_DAYS + 1) * DAY_MS - 1 // 90 days from day 1 start to day 90 end
}