package ru.rznnike.eyehealthmanager.app.presentation.acuity.info

import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType

interface AcuityInfoView : NavigationMvpView {
    @AddToEndSingle
    fun populateData(
        symbolsType: AcuityTestSymbolsType,
        eyesType: TestEyesType
    )

    @OneExecution
    fun showDayPartSelectionDialog(replaceBeginningWithMorning: Boolean)
}
