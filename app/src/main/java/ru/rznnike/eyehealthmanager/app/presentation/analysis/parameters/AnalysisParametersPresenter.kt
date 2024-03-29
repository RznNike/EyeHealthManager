package ru.rznnike.eyehealthmanager.app.presentation.analysis.parameters

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.presenterScope
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.domain.interactor.analysis.GetAnalysisResultUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetApplyDynamicCorrectionsUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.SetApplyDynamicCorrectionsUseCase
import ru.rznnike.eyehealthmanager.domain.model.AnalysisParameters
import ru.rznnike.eyehealthmanager.domain.model.enums.AnalysisType
import ru.rznnike.eyehealthmanager.domain.model.exception.NotEnoughDataException
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.atEndOfDay
import ru.rznnike.eyehealthmanager.domain.utils.millis
import ru.rznnike.eyehealthmanager.domain.utils.toLocalDate
import java.time.Clock
import java.time.temporal.ChronoUnit

@InjectViewState
class AnalysisParametersPresenter : BasePresenter<AnalysisParametersView>() {
    private val clock: Clock by inject()
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val getApplyDynamicCorrectionsUseCase: GetApplyDynamicCorrectionsUseCase by inject()
    private val setApplyDynamicCorrectionsUseCase: SetApplyDynamicCorrectionsUseCase by inject()
    private val getAnalysisResultUseCase: GetAnalysisResultUseCase by inject()

    private val parameters = AnalysisParameters()

    override fun onFirstViewAttach() {
        initData()
    }

    private fun initData() {
        presenterScope.launch {
            val dateNow = clock.millis().toLocalDate()
            parameters.apply {
                dateFrom = dateNow.minusDays(GlobalConstants.ANALYSIS_MAX_RANGE_DAYS - 1).atStartOfDay().millis()
                dateTo = dateNow.atEndOfDay().millis()
                applyDynamicCorrections = getApplyDynamicCorrectionsUseCase().data ?: false
            }
            viewState.populateData(parameters)
        }
    }

    fun onDateFromValueChanged(newValue: Long) {
        parameters.dateFrom = newValue.toLocalDate().atStartOfDay().millis()
        val deltaDays = ChronoUnit.DAYS.between(parameters.dateFrom.toLocalDate(), parameters.dateTo.toLocalDate())
        when {
            deltaDays < (GlobalConstants.ANALYSIS_MIN_RANGE_DAYS - 1) -> {
                parameters.dateTo = newValue.toLocalDate()
                    .plusDays(GlobalConstants.ANALYSIS_MIN_RANGE_DAYS - 1)
                    .atEndOfDay()
                    .millis()
            }
            deltaDays > (GlobalConstants.ANALYSIS_MAX_RANGE_DAYS - 1) -> {
                parameters.dateTo = newValue.toLocalDate()
                    .plusDays(GlobalConstants.ANALYSIS_MAX_RANGE_DAYS - 1)
                    .atEndOfDay()
                    .millis()
            }
        }
        viewState.populateData(parameters)
    }

    fun onDateToValueChanged(newValue: Long) {
        parameters.dateTo = newValue.toLocalDate().atEndOfDay().millis()
        val deltaDays = ChronoUnit.DAYS.between(parameters.dateFrom.toLocalDate(), parameters.dateTo.toLocalDate())
        when {
            deltaDays < (GlobalConstants.ANALYSIS_MIN_RANGE_DAYS - 1) -> {
                parameters.dateFrom = newValue.toLocalDate()
                    .minusDays(GlobalConstants.ANALYSIS_MIN_RANGE_DAYS - 1)
                    .atStartOfDay()
                    .millis()
            }
            deltaDays > (GlobalConstants.ANALYSIS_MAX_RANGE_DAYS - 1) -> {
                parameters.dateFrom = newValue.toLocalDate()
                    .minusDays(GlobalConstants.ANALYSIS_MAX_RANGE_DAYS - 1)
                    .atStartOfDay()
                    .millis()
            }
        }
        viewState.populateData(parameters)
    }

    fun onAnalysisTypeValueChanged(newValue: AnalysisType) {
        parameters.analysisType = newValue
        viewState.populateData(parameters)
    }

    fun onApplyDynamicCorrectionsChanged(newValue: Boolean) {
        presenterScope.launch {
            parameters.applyDynamicCorrections = newValue
            viewState.populateData(parameters)
            setApplyDynamicCorrectionsUseCase(newValue)
        }
    }

    fun startAnalysis() {
        presenterScope.launch {
            viewState.setProgress(true)
            getAnalysisResultUseCase(parameters).process(
                { result ->
                    viewState.routerNewRootScreen(
                        Screens.Screen.analysisResult(result)
                    )
                }, { error ->
                    if (error is NotEnoughDataException) {
                        viewState.showNotEnoughDataMessage()
                    } else {
                        errorHandler.proceed(error) {
                            notifier.sendMessage(it)
                        }
                    }
                    viewState.routerFinishFlow()
                }
            )
            viewState.setProgress(false)
        }
    }
}