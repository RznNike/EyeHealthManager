package ru.rznnike.eyehealthmanager.app.presentation.analysis.result

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.presenterScope
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.domain.interactor.analysis.GetAnalysisResultUseCase
import ru.rznnike.eyehealthmanager.domain.model.AnalysisParams

@InjectViewState
class AnalysisResultPresenter(
    private val params: AnalysisParams?
) : BasePresenter<AnalysisResultView>() {
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val getAnalysisResultUseCase: GetAnalysisResultUseCase by inject()

    override fun onFirstViewAttach() {
        presenterScope.launch {
            if (params == null) {
                viewState.routerFinishFlow()
                notifier.sendMessage(R.string.error)
            } else {
                viewState.setProgress(true)
                getAnalysisResultUseCase(params).process(
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
}