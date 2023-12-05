package ru.rznnike.eyehealthmanager.app.presentation.analysis.params

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView
import ru.rznnike.eyehealthmanager.domain.model.AnalysisParams

interface AnalysisParamsView : NavigationMvpView {
    @AddToEndSingle
    fun populateData(params: AnalysisParams)
}
