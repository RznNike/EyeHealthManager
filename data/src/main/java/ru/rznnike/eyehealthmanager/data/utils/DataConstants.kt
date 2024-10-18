package ru.rznnike.eyehealthmanager.data.utils

object DataConstants {
    const val EXPORT_PAGE_SIZE = 100
    const val IMPORT_PAGE_SIZE = 100
    const val APP_DIR = "Eye Health Manager"
    const val EXPORT_DIR = "export"

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