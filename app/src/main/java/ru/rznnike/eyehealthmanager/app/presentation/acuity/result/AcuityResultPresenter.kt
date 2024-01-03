package ru.rznnike.eyehealthmanager.app.presentation.acuity.result

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.presenterScope
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.dispatcher.event.AppEvent
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.domain.interactor.analysis.GetAnalysisResultUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.test.DeleteTestResultUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetApplyDynamicCorrectionsUseCase
import ru.rznnike.eyehealthmanager.domain.model.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.AnalysisParameters
import ru.rznnike.eyehealthmanager.domain.model.AnalysisResult
import ru.rznnike.eyehealthmanager.domain.model.enums.AnalysisType
import ru.rznnike.eyehealthmanager.domain.model.exception.NotEnoughDataException
import ru.rznnike.eyehealthmanager.domain.utils.atEndOfDay
import ru.rznnike.eyehealthmanager.domain.utils.getTodayCalendar
import java.util.Calendar

@InjectViewState
class AcuityResultPresenter(
    private val testResult: AcuityTestResult
) : BasePresenter<AcuityResultView>() {
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val getApplyDynamicCorrectionsUseCase: GetApplyDynamicCorrectionsUseCase by inject()
    private val getAnalysisResultUseCase: GetAnalysisResultUseCase by inject()
    private val deleteTestResultUseCase: DeleteTestResultUseCase by inject()

    private var analysisResult: AnalysisResult? = null
    private var applyDynamicCorrections = false

    override fun onFirstViewAttach() {
        presenterScope.launch {
            viewState.setProgress(true)
            applyDynamicCorrections = getApplyDynamicCorrectionsUseCase().data ?: false
            val parameters = AnalysisParameters(
                dateFrom = getTodayCalendar().apply {
                    add(Calendar.MONTH, -1)
                }.timeInMillis,
                dateTo = Calendar.getInstance().atEndOfDay().timeInMillis,
                applyDynamicCorrections = applyDynamicCorrections,
                analysisType = AnalysisType.ACUITY_ONLY
            )
            getAnalysisResultUseCase(parameters).process(
                {
                    analysisResult = it
                    populateData()
                }, { error ->
                    errorHandler.proceed(error) {
                        if (error !is NotEnoughDataException) {
                            notifier.sendMessage(it)
                        }
                        populateData()
                    }
                }
            )
            viewState.setProgress(false)
        }
    }

    fun onApplyDynamicCorrectionsChanged(value: Boolean) {
        applyDynamicCorrections = value
        populateData()
    }

    fun redoTest() {
        presenterScope.launch {
            viewState.setProgress(true)
            deleteTestResultUseCase(testResult.id).process(
                {
                    viewState.routerNewRootScreen(Screens.Screen.acuityTest(testResult.dayPart))
                }, { error ->
                    errorHandler.proceed(error) {
                        notifier.sendMessage(it)
                    }
                }
            )
            eventDispatcher.sendEvent(AppEvent.JournalChanged)
            viewState.setProgress(false)
        }
    }

    private fun populateData() =
        viewState.populateData(
            testResult = testResult,
            analysisResult = analysisResult,
            applyDynamicCorrections = applyDynamicCorrections
        )
}
