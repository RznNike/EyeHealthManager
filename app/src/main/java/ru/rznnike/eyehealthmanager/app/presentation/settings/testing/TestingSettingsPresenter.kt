package ru.rznnike.eyehealthmanager.app.presentation.settings.testing

import com.fredporciuncula.flow.preferences.Preference
import moxy.InjectViewState
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.data.preference.PreferencesWrapper
import java.util.*

private const val MIN_DELTA_IN_MS = 60 * 1000L // 1m
private const val DAY_LENGTH_IN_MS = 24 * 60 * 60 * 1000L // 24h

@InjectViewState
class TestingSettingsPresenter : BasePresenter<TestingSettingsView>() {
    private val preferences: PreferencesWrapper by inject()

    override fun onFirstViewAttach() {
        populateData()
    }

    private fun populateData() {
        viewState.populateData(
            preferences.armsLength.get(),
            preferences.dotsPerMillimeter.get(),
            preferences.replaceBeginningWithMorning.get(),
            preferences.enableAutoDayPart.get(),
            preferences.timeToDayBeginning.get(),
            preferences.timeToDayMiddle.get(),
            preferences.timeToDayEnd.get()
        )
    }

    fun onCheckBoxReplaceBeginningClicked(checked: Boolean) {
        preferences.replaceBeginningWithMorning.set(checked)
        populateData()
    }

    fun onCheckBoxAutoDayPartSelectionClicked(checked: Boolean) {
        preferences.enableAutoDayPart.set(checked)
        populateData()
    }

    fun onTimeToDayBeginningValueChanged(timestamp: Long) {
        preferences.timeToDayBeginning.set(timestampToDayTime(timestamp))

        syncTime(preferences.timeToDayBeginning, preferences.timeToDayMiddle)
        syncTime(preferences.timeToDayBeginning, preferences.timeToDayEnd)
        syncTime(preferences.timeToDayMiddle, preferences.timeToDayEnd)
        fixTimesOrder(TimeCheckBase.BEGINNING)

        populateData()
    }

    fun onTimeToDayMiddleValueChanged(timestamp: Long) {
        preferences.timeToDayMiddle.set(timestampToDayTime(timestamp))

        syncTime(preferences.timeToDayBeginning, preferences.timeToDayMiddle)
        syncTime(preferences.timeToDayMiddle, preferences.timeToDayEnd)
        fixTimesOrder(TimeCheckBase.MIDDLE)

        populateData()
    }

    fun onTimeToDayEndValueChanged(timestamp: Long) {
        preferences.timeToDayEnd.set(timestampToDayTime(timestamp))

        syncTime(preferences.timeToDayEnd, preferences.timeToDayBeginning)
        syncTime(preferences.timeToDayMiddle, preferences.timeToDayEnd)
        fixTimesOrder(TimeCheckBase.END)

        populateData()
    }

    private fun timestampToDayTime(timestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }
        return (calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60 + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND)) * 1000L
    }

    private fun syncTime(basePreference: Preference<Long>, syncPreference: Preference<Long>) {
        var timeToSync = syncPreference.get()
        if (timeToSync == basePreference.get()) {
            timeToSync += MIN_DELTA_IN_MS
            if (timeToSync >= DAY_LENGTH_IN_MS) {
                timeToSync -= DAY_LENGTH_IN_MS
            }
        }
        if (timeToSync != syncPreference.get()) {
            syncPreference.set(timeToSync)
        }
    }

    private fun fixTimesOrder(timeCheckBase: TimeCheckBase) {
        val timeToDayBeginning = preferences.timeToDayBeginning.get()
        var timeToDayMiddle = preferences.timeToDayMiddle.get()
        var timeToDayEnd = preferences.timeToDayEnd.get()

        if (timeToDayMiddle < timeToDayBeginning) {
            timeToDayMiddle += DAY_LENGTH_IN_MS
        }
        if (timeToDayEnd < timeToDayBeginning) {
            timeToDayEnd += DAY_LENGTH_IN_MS
        }

        if (timeToDayEnd < timeToDayMiddle) {
            when (timeCheckBase) {
                TimeCheckBase.BEGINNING -> {
                    val temp = preferences.timeToDayMiddle.get()
                    preferences.timeToDayMiddle.set(preferences.timeToDayEnd.get())
                    preferences.timeToDayEnd.set(temp)
                }
                TimeCheckBase.MIDDLE -> {
                    val temp = preferences.timeToDayBeginning.get()
                    preferences.timeToDayBeginning.set(preferences.timeToDayEnd.get())
                    preferences.timeToDayEnd.set(temp)
                }
                TimeCheckBase.END -> {
                    val temp = preferences.timeToDayMiddle.get()
                    preferences.timeToDayMiddle.set(preferences.timeToDayBeginning.get())
                    preferences.timeToDayBeginning.set(temp)
                }
            }
        }
    }

    fun onArmsLengthValueChanged(value: String) {
        value.toIntOrNull()?.let { centimeters ->
            val millimeters = centimeters * 10
            preferences.armsLength.set(millimeters)
        }
        populateData()
    }

    fun onBodyHeightValueChanged(value: String) {
        value.toIntOrNull()?.let { centimeters ->
            val millimeters = (centimeters * 10 * 0.9 / 2).toInt()   // arm = (height - 10%) / 2
            preferences.armsLength.set(millimeters)
        }
        populateData()
    }

    fun onScalingLineLengthValueChanged(value: String, lineWidthPx: Int) {
        value.toFloatOrNull()?.let { centimeters ->
            val millimeters = centimeters * 10
            val dpmm = lineWidthPx / millimeters
            preferences.dotsPerMillimeter.set(dpmm)
        }
        populateData()
    }

    fun resetScale() {
        preferences.dotsPerMillimeter.delete()
        populateData()
    }

    private enum class TimeCheckBase {
        BEGINNING,
        MIDDLE,
        END
    }
}