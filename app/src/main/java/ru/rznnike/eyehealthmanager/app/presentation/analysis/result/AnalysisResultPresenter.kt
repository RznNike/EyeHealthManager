package ru.rznnike.eyehealthmanager.app.presentation.analysis.result

import moxy.InjectViewState
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.domain.model.AnalysisResult

@InjectViewState
class AnalysisResultPresenter(
    private val result: AnalysisResult
) : BasePresenter<AnalysisResultView>() {
    override fun onFirstViewAttach() {
        viewState.populateData(result)
    }
}