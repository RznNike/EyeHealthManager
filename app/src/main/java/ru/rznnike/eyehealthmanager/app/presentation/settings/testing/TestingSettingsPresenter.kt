package ru.rznnike.eyehealthmanager.app.presentation.settings.testing

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.presenterScope
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.app.dispatcher.event.AppEvent
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.domain.global.CoroutineProvider
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.SetTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.model.TestingSettings
import ru.rznnike.eyehealthmanager.domain.utils.getDayTime

private const val MIN_DELTA_IN_MS = 60 * 1000L // 1m
private const val DAY_LENGTH_IN_MS = 24 * 60 * 60 * 1000L // 24h

@InjectViewState
class TestingSettingsPresenter : BasePresenter<TestingSettingsView>() {
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val coroutineProvider: CoroutineProvider by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val getTestingSettingsUseCase: GetTestingSettingsUseCase by inject()
    private val setTestingSettingsUseCase: SetTestingSettingsUseCase by inject()

    private var settings: TestingSettings = TestingSettings()

    override fun onFirstViewAttach() {
        loadData()
    }

    fun onPause() {
        coroutineProvider.scopeIo.launch {
            setTestingSettingsUseCase(settings).process(
                {
                    eventDispatcher.sendEvent(AppEvent.TestingSettingsChanged)
                }
            )
        }
    }

    private fun loadData() {
        presenterScope.launch {
            getTestingSettingsUseCase().process(
                { result ->
                    settings = result
                    populateData()
                }, { error ->
                    errorHandler.proceed(error) {
                        notifier.sendMessage(it)
                        viewState.routerExit()
                    }
                }
            )
        }
    }

    private fun populateData() = viewState.populateData(settings)

    fun onCheckBoxReplaceBeginningClicked(checked: Boolean) {
        settings.replaceBeginningWithMorning = checked
        populateData()
    }

    fun onCheckBoxAutoDayPartSelectionClicked(checked: Boolean) {
        settings.enableAutoDayPart = checked
        populateData()
    }

    fun onTimeToDayBeginningValueChanged(timestamp: Long) {
        settings.timeToDayBeginning = timestamp.getDayTime()
        fixTimeSelection(settings, TimePeriod.MIDDLE) {
            fixTimeSelection(settings, TimePeriod.END)
        }
        populateData()
    }

    fun onTimeToDayMiddleValueChanged(timestamp: Long) {
        settings.timeToDayMiddle = timestamp.getDayTime()
        fixTimeSelection(settings, TimePeriod.END) {
            fixTimeSelection(settings, TimePeriod.BEGINNING)
        }
        populateData()
    }

    fun onTimeToDayEndValueChanged(timestamp: Long) {
        settings.timeToDayEnd = timestamp.getDayTime()
        fixTimeSelection(settings, TimePeriod.BEGINNING) {
            fixTimeSelection(settings, TimePeriod.MIDDLE)
        }
        populateData()
    }

    private fun fixTimeSelection(
        settings: TestingSettings,
        period: TimePeriod,
        onFixAppliedCallback: (() -> Unit)? = null
    ) {
        fun isTimeOrderCorrect(settings: TestingSettings): Boolean {
            val middle = if (settings.timeToDayMiddle >= settings.timeToDayBeginning) {
                settings.timeToDayMiddle
            } else {
                settings.timeToDayMiddle + DAY_LENGTH_IN_MS
            }
            val end = if (settings.timeToDayEnd >= settings.timeToDayBeginning) {
                settings.timeToDayEnd
            } else {
                settings.timeToDayEnd + DAY_LENGTH_IN_MS
            }

            return (middle > settings.timeToDayBeginning) && (end > middle)
        }

        if (!isTimeOrderCorrect(settings)) {
            when (period) {
                TimePeriod.BEGINNING -> {
                    settings.timeToDayBeginning = settings.timeToDayEnd + MIN_DELTA_IN_MS
                    if (settings.timeToDayBeginning >= DAY_LENGTH_IN_MS) {
                        settings.timeToDayBeginning -= DAY_LENGTH_IN_MS
                    }
                }
                TimePeriod.MIDDLE -> {
                    settings.timeToDayMiddle = settings.timeToDayBeginning + MIN_DELTA_IN_MS
                    if (settings.timeToDayMiddle >= DAY_LENGTH_IN_MS) {
                        settings.timeToDayMiddle -= DAY_LENGTH_IN_MS
                    }
                }
                TimePeriod.END -> {
                    settings.timeToDayEnd = settings.timeToDayMiddle + MIN_DELTA_IN_MS
                    if (settings.timeToDayEnd >= DAY_LENGTH_IN_MS) {
                        settings.timeToDayEnd -= DAY_LENGTH_IN_MS
                    }
                }
            }
            onFixAppliedCallback?.invoke()
        }
    }

    fun onArmsLengthValueChanged(value: String) {
        value.toIntOrNull()?.let { centimeters ->
            settings.armsLength = centimeters * 10
        }
        populateData()
    }

    fun onBodyHeightValueChanged(value: String) {
        value.toIntOrNull()?.let { centimeters ->
            settings.armsLength = (centimeters * 10 * 0.9 / 2).toInt()   // arm = (height - 10%) / 2
        }
        populateData()
    }

    fun onScalingLineLengthValueChanged(value: String, lineWidthPx: Int) {
        value.toFloatOrNull()?.let { centimeters ->
            settings.dpmm = lineWidthPx / centimeters / 10
        }
        populateData()
    }

    fun resetScale() {
        settings.dpmm = -1f
        populateData()
    }

    private enum class TimePeriod {
        BEGINNING,
        MIDDLE,
        END
    }
}