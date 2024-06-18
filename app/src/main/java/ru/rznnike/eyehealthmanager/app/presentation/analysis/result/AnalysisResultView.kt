package ru.rznnike.eyehealthmanager.app.presentation.analysis.result

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView
import ru.rznnike.eyehealthmanager.domain.model.analysis.AnalysisResult

interface AnalysisResultView : NavigationMvpView {
    @AddToEndSingle
    fun populateData(analysisResult: AnalysisResult)
}
