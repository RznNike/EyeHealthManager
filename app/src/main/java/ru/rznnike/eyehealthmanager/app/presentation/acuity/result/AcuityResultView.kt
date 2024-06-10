package ru.rznnike.eyehealthmanager.app.presentation.acuity.result

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.analysis.AnalysisResult

interface AcuityResultView : NavigationMvpView {
    @AddToEndSingle
    fun setProgress(show: Boolean, immediately: Boolean = true)

    @AddToEndSingle
    fun populateData(
        testResult: AcuityTestResult,
        analysisResult: AnalysisResult?,
        applyDynamicCorrections: Boolean
    )
}
