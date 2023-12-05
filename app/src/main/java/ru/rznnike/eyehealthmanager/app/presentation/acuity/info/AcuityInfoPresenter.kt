package ru.rznnike.eyehealthmanager.app.presentation.acuity.info

import moxy.InjectViewState
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.data.preference.PreferencesWrapper
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.enums.DayPart
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType
import java.util.*

@InjectViewState
class AcuityInfoPresenter : BasePresenter<AcuityInfoView>() {
    private val preferences: PreferencesWrapper by inject()

    override fun onFirstViewAttach() {
        populateData()
    }

    fun onSymbolsTypeSelected(symbolsType: AcuityTestSymbolsType) {
        preferences.acuitySymbolsType.set(symbolsType.id)
        populateData()
    }

    fun onEyesTypeSelected(eyesType: TestEyesType) {
        preferences.testEyesType.set(eyesType.id)
        populateData()
    }

    fun onScaleSettings() {
        viewState.routerNavigateTo(Screens.Screen.testingSettings())
    }

    fun onDayPartAutoSelectionSettings() {
        viewState.routerNavigateTo(Screens.Screen.testingSettings())
    }

    fun onStart(dayPart: DayPart) {
        viewState.routerNavigateTo(Screens.Screen.acuityInstruction(dayPart))
    }

    private fun populateData() {
        viewState.populateData(
            AcuityTestSymbolsType[preferences.acuitySymbolsType.get()],
            TestEyesType[preferences.testEyesType.get()]
        )
    }

    fun onPrepareToStartTest() {
        if (preferences.enableAutoDayPart.get()) {
            val dayPart = autoSelectDayPart()
            onStart(dayPart)
        } else {
            viewState.showDayPartSelectionDialog(preferences.replaceBeginningWithMorning.get())
        }
    }

    private fun autoSelectDayPart(): DayPart {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis() - calendar.timeZone.rawOffset
        val dayTime = (calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60 +
                calendar.get(Calendar.MINUTE) * 60 +
                calendar.get(Calendar.SECOND)) * 1000L

        val timeToBeginning = preferences.timeToDayBeginning.get()
        val timeToMiddle = preferences.timeToDayMiddle.get()
        val timeToEnd = preferences.timeToDayEnd.get()
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
