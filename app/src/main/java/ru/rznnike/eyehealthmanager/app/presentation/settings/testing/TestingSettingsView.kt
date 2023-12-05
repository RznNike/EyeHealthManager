package ru.rznnike.eyehealthmanager.app.presentation.settings.testing

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView

interface TestingSettingsView : NavigationMvpView {
    @AddToEndSingle
    fun populateData(
        armsLength: Int,
        dpmm: Float,
        replaceBeginningWithMorning: Boolean,
        enableAutoDayPart: Boolean,
        timeToDayBeginning: Long,
        timeToDayMiddle: Long,
        timeToDayEnd: Long
    )
}
