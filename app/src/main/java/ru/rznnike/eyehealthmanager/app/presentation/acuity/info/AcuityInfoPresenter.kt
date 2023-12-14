package ru.rznnike.eyehealthmanager.app.presentation.acuity.info

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.presenterScope
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetAcuityTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.SetAcuityTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.model.AcuityTestingSettings
import ru.rznnike.eyehealthmanager.domain.model.TestingSettings
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.enums.DayPart
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType
import java.util.Calendar

@InjectViewState
class AcuityInfoPresenter : BasePresenter<AcuityInfoView>() {
    private val getTestingSettingsUseCase: GetTestingSettingsUseCase by inject()
    private val getAcuityTestingSettingsUseCase: GetAcuityTestingSettingsUseCase by inject()
    private val setAcuityTestingSettingsUseCase: SetAcuityTestingSettingsUseCase by inject() // TODO save settings on exit

    private var generalSettings = TestingSettings()
    private var acuitySettings = AcuityTestingSettings()

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

    fun onStartTest(dayPart: DayPart) =
        viewState.routerNavigateTo(Screens.Screen.acuityInstruction(dayPart))

    fun onPrepareToStartTest() {
        if (generalSettings.enableAutoDayPart) {
            val dayPart = autoSelectDayPart()
            onStartTest(dayPart)
        } else {
            viewState.showDayPartSelectionDialog(generalSettings.replaceBeginningWithMorning)
        }
    }

    private fun autoSelectDayPart(): DayPart { // TODO refactor
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis() - calendar.timeZone.rawOffset
        val dayTime = (calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60 +
                calendar.get(Calendar.MINUTE) * 60 +
                calendar.get(Calendar.SECOND)) * 1000L

        val timeToBeginning = generalSettings.timeToDayBeginning
        val timeToMiddle = generalSettings.timeToDayMiddle
        val timeToEnd = generalSettings.timeToDayEnd
        return if (((dayTime >= timeToBeginning) && (dayTime < timeToMiddle))
            || ((dayTime >= timeToBeginning) && (timeToBeginning > timeToMiddle))
            || ((dayTime < timeToMiddle) && (timeToBeginning > timeToMiddle))) {
            DayPart.BEGINNING
        } else if (((dayTime >= timeToMiddle) && (dayTime < timeToEnd))
            || ((dayTime >= timeToMiddle) && (timeToMiddle > timeToEnd))
            || ((dayTime < timeToEnd) && (timeToMiddle > timeToEnd))) {
            DayPart.MIDDLE
        } else {
            DayPart.END
        }
    }

    fun onAddDoctorResult() {
        viewState.routerNavigateTo(Screens.Screen.acuityDoctorResult())
    }
}
