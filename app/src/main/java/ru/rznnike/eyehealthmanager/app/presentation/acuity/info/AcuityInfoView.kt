package ru.rznnike.eyehealthmanager.app.presentation.acuity.info

import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView
import ru.rznnike.eyehealthmanager.domain.model.AcuityTestingSettings

interface AcuityInfoView : NavigationMvpView {
    @AddToEndSingle
    fun populateData(acuitySettings: AcuityTestingSettings)

    @OneExecution
    fun showDayPartSelectionDialog(replaceBeginningWithMorning: Boolean)
}
