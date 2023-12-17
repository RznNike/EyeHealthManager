package ru.rznnike.eyehealthmanager.app.presentation.analysis.result

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.presenterScope
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.domain.interactor.analysis.GetAnalysisResultUseCase
import ru.rznnike.eyehealthmanager.domain.model.AnalysisParameters

@InjectViewState
class AnalysisResultPresenter(
    private val parameters: AnalysisParameters
) : BasePresenter<AnalysisResultView>() {
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val getAnalysisResultUseCase: GetAnalysisResultUseCase by inject()

    override fun onFirstViewAttach() {
        presenterScope.launch {
            viewState.setProgress(true)
            getAnalysisResultUseCase(parameters).process(
                {
                    viewState.populateData(it)
                }, { error ->
                    errorHandler.proceed(error) {
                        notifier.sendMessage(it)
                        viewState.routerFinishFlow()
                    }
                }
            )
            viewState.setProgress(false)
        }
    }
}