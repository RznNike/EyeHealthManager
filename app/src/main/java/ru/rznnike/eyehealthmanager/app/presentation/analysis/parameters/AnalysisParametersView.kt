package ru.rznnike.eyehealthmanager.app.presentation.analysis.parameters

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView
import ru.rznnike.eyehealthmanager.domain.model.AnalysisParameters

interface AnalysisParametersView : NavigationMvpView {
    @AddToEndSingle
    fun populateData(parameters: AnalysisParameters)
}
