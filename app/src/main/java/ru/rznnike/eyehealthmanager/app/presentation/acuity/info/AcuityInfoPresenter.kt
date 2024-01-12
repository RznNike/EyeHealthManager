package ru.rznnike.eyehealthmanager.app.presentation.acuity.info

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.presenterScope
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.dispatcher.event.AppEvent
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetAcuityTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.SetAcuityTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.model.AcuityTestingSettings
import ru.rznnike.eyehealthmanager.domain.model.TestingSettings
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.enums.DayPart
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType
import ru.rznnike.eyehealthmanager.domain.utils.getDayTime
import java.time.Clock

@InjectViewState
class AcuityInfoPresenter : BasePresenter<AcuityInfoView>(), EventDispatcher.EventListener {
    private val clock: Clock by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val getTestingSettingsUseCase: GetTestingSettingsUseCase by inject()
    private val getAcuityTestingSettingsUseCase: GetAcuityTestingSettingsUseCase by inject()
    private val setAcuityTestingSettingsUseCase: SetAcuityTestingSettingsUseCase by inject()

    private var generalSettings = TestingSettings()
    private var acuitySettings = AcuityTestingSettings()

    init {
        subscribeToEvents()
    }

    override fun onDestroy() {
        eventDispatcher.removeEventListener(this)
    }

    private fun subscribeToEvents() {
        eventDispatcher.addEventListener(AppEvent.TestingSettingsChanged::class, this)
    }

    override fun onEvent(event: AppEvent) {
        when (event) {
            is AppEvent.TestingSettingsChanged -> loadData()
            else -> Unit
        }
    }

    override fun onFirstViewAttach() {
        loadData()
    }

    private fun loadData() {
        presenterScope.launch {
            generalSettings = getTestingSettingsUseCase().data ?: TestingSettings()
            acuitySettings = getAcuityTestingSettingsUseCase().data ?: AcuityTestingSettings()
            populateData()
        }
    }

    private fun populateData() = viewState.populateData(acuitySettings)

    fun onSymbolsTypeSelected(newValue: AcuityTestSymbolsType) {
        acuitySettings.symbolsType = newValue
        populateData()
    }

    fun onEyesTypeSelected(newValue: TestEyesType) {
        acuitySettings.eyesType = newValue
        populateData()
    }

    fun onScaleSettings() = viewState.routerNavigateTo(Screens.Screen.testingSettings())

    fun onDayPartAutoSelectionSettings() =
        viewState.routerNavigateTo(Screens.Screen.testingSettings())

    fun startTest(dayPart: DayPart) {
        presenterScope.launch {
            setAcuityTestingSettingsUseCase(acuitySettings)
            viewState.routerNavigateTo(Screens.Screen.acuityInstruction(dayPart))
        }
    }

    fun onPrepareToStartTest() {
        if (generalSettings.enableAutoDayPart) {
            val dayPart = autoSelectDayPart()
            startTest(dayPart)
        } else {
            viewState.showDayPartSelectionDialog(generalSettings.replaceBeginningWithMorning)
        }
    }

    private fun autoSelectDayPart() =
        when (val currentDayTime = clock.millis().getDayTime()) {
            generalSettings.timeToDayBeginning -> DayPart.BEGINNING
            generalSettings.timeToDayMiddle -> DayPart.MIDDLE
            generalSettings.timeToDayEnd -> DayPart.END
            else -> {
                val sortedTimes = listOf(
                    currentDayTime,
                    generalSettings.timeToDayBeginning,
                    generalSettings.timeToDayMiddle,
                    generalSettings.timeToDayEnd
                ).sorted()
                val currentIndex = sortedTimes.indexOf(currentDayTime)
                val dayPartIndex = if (currentIndex > 0) currentIndex - 1 else sortedTimes.lastIndex

                when (sortedTimes[dayPartIndex]) {
                    generalSettings.timeToDayMiddle -> DayPart.MIDDLE
                    generalSettings.timeToDayEnd -> DayPart.END
                    else -> DayPart.BEGINNING
                }
            }
        }

    fun onAddDoctorResult() = viewState.routerNavigateTo(Screens.Screen.acuityDoctorResult())
}
