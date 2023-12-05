package ru.rznnike.eyehealthmanager.data.preference

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.fredporciuncula.flow.preferences.Preference
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.enums.Language
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType

private const val LANGUAGE = "LANGUAGE"
private const val ACUITY_SYMBOLS_TYPE = "ACUITY_SYMBOLS_TYPE_1"
private const val TEST_EYES_TYPE = "TEST_EYES_TYPE_1"
private const val DOTS_PER_MILLIMETER = "DOTS_PER_MILLIMETER"
private const val ARMS_LENGTH = "ARMS_LENGTH"
private const val ARMS_LENGTH_DEFAULT = 800
private const val REPLACE_BEGINNING_WITH_MORNING = "REPLACE_BEGINNING_WITH_MORNING"
private const val ENABLE_AUTO_DAY_PART = "ENABLE_AUTO_DAY_PART"
private const val TIME_TO_DAY_BEGINNING = "TIME_TO_DAY_BEGINNING"
private const val TIME_TO_DAY_BEGINNING_DEFAULT = 21_600_000L // 06:00
private const val TIME_TO_DAY_MIDDLE = "TIME_TO_DAY_MIDDLE"
private const val TIME_TO_DAY_MIDDLE_DEFAULT = 43_200_000L // 12:00
private const val TIME_TO_DAY_END = "TIME_TO_DAY_END"
private const val TIME_TO_DAY_END_DEFAULT = 64_800_000L // 18:00
private const val APPLY_DYNAMIC_CORRECTIONS_IN_ANALYSIS = "APPLY_DYNAMIC_CORRECTIONS_IN_ANALYSIS"
private const val WELCOME_DIALOG_SHOWED = "WELCOME_DIALOG_SHOWED"
private const val DISPLAYED_CHANGELOG_VERSION = "DISPLAYED_CHANGELOG_VERSION"
private const val NOTIFICATIONS_SOUND_ENABLED = "NOTIFICATIONS_SOUND_ENABLED"

class PreferencesWrapper(private val preferences: FlowSharedPreferences) {
    val language: Preference<String>
        get() = preferences.getString(LANGUAGE, Language.EN.tag)
    val acuitySymbolsType: Preference<Int>
        get() = preferences.getInt(ACUITY_SYMBOLS_TYPE, AcuityTestSymbolsType.LETTERS_RU.id)
    val testEyesType: Preference<Int>
        get() = preferences.getInt(TEST_EYES_TYPE, TestEyesType.BOTH.id)
    val dotsPerMillimeter: Preference<Float>
        get() = preferences.getFloat(DOTS_PER_MILLIMETER, -1f)
    val armsLength: Preference<Int>
        get() = preferences.getInt(ARMS_LENGTH, ARMS_LENGTH_DEFAULT)
    val replaceBeginningWithMorning: Preference<Boolean>
        get() = preferences.getBoolean(REPLACE_BEGINNING_WITH_MORNING, false)
    val enableAutoDayPart: Preference<Boolean>
        get() = preferences.getBoolean(ENABLE_AUTO_DAY_PART, false)
    val timeToDayBeginning: Preference<Long>
        get() = preferences.getLong(TIME_TO_DAY_BEGINNING, TIME_TO_DAY_BEGINNING_DEFAULT)
    val timeToDayMiddle: Preference<Long>
        get() = preferences.getLong(TIME_TO_DAY_MIDDLE, TIME_TO_DAY_MIDDLE_DEFAULT)
    val timeToDayEnd: Preference<Long>
        get() = preferences.getLong(TIME_TO_DAY_END, TIME_TO_DAY_END_DEFAULT)
    val applyDynamicCorrectionsInAnalysis: Preference<Boolean>
        get() = preferences.getBoolean(APPLY_DYNAMIC_CORRECTIONS_IN_ANALYSIS, true)
    val welcomeDialogShowed: Preference<Boolean>
        get() = preferences.getBoolean(WELCOME_DIALOG_SHOWED, false)
    val displayedChangelogVersion: Preference<Int>
        get() = preferences.getInt(DISPLAYED_CHANGELOG_VERSION, -1)
    val notificationsSoundEnabled: Preference<Boolean>
        get() = preferences.getBoolean(NOTIFICATIONS_SOUND_ENABLED, true)
}
