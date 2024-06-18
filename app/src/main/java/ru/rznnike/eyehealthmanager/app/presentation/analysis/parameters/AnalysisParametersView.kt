package ru.rznnike.eyehealthmanager.app.presentation.analysis.parameters

import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView
import ru.rznnike.eyehealthmanager.domain.model.analysis.AnalysisParameters

interface AnalysisParametersView : NavigationMvpView {
    @AddToEndSingle
    fun setProgress(show: Boolean, immediately: Boolean = true)

    @AddToEndSingle
    fun populateData(parameters: AnalysisParameters)

    @OneExecution
    fun showNotEnoughDataMessage()
}
