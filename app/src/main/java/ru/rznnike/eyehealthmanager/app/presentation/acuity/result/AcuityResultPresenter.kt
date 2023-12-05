package ru.rznnike.eyehealthmanager.app.presentation.acuity.result

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.presenterScope
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.data.preference.PreferencesWrapper
import ru.rznnike.eyehealthmanager.domain.interactor.analysis.GetAnalysisResultUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.test.DeleteTestResultUseCase
import ru.rznnike.eyehealthmanager.domain.model.*
import ru.rznnike.eyehealthmanager.domain.model.enums.AnalysisType
import ru.rznnike.eyehealthmanager.domain.model.exception.NotEnoughDataException
import java.util.*

@InjectViewState
class AcuityResultPresenter(
    private val result: AcuityTestResult
) : BasePresenter<AcuityResultView>() {
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val preferences: PreferencesWrapper by inject()
    private val getAnalysisResultUseCase: GetAnalysisResultUseCase by inject()
    private val deleteTestResultUseCase: DeleteTestResultUseCase by inject()

    private var testResult = AcuityTestResult()
    private var analysisResult: AnalysisResult? = null
    private var applyDynamicCorrections = true

    override fun onFirstViewAttach() {
        presenterScope.launch {
            testResult = result
            viewState.setProgress(true)
            val params = AnalysisParams(
                dateFrom = Calendar.getInstance()
                    .apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                        add(Calendar.MONTH, -1)
                    }
                    .timeInMillis,
                dateTo = Calendar.getInstance()
                    .apply {
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                        set(Calendar.MILLISECOND, 999)
                    }
                    .timeInMillis,
                applyDynamicCorrectionsInAnalysis = preferences.applyDynamicCorrectionsInAnalysis.get(),
                analysisType = AnalysisType.ACUITY_ONLY
            )
            getAnalysisResultUseCase(params).process(
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

    fun onCheckBoxApplyDynamicCorrectionsClicked(value: Boolean) {
        applyDynamicCorrections = value
        populateData()
    }

    fun onRedoTest() {
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
