package ru.rznnike.eyehealthmanager.app.presentation.analysis.result

import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView
import ru.rznnike.eyehealthmanager.domain.model.AnalysisResult

interface AnalysisResultView : NavigationMvpView {
    @AddToEndSingle
    fun populateData(analysisResult: AnalysisResult)
}
