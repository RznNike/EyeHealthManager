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
import ru.rznnike.eyehealthmanager.domain.utils.atStartOfDay
import ru.rznnike.eyehealthmanager.domain.utils.getTodayCalendar
import ru.rznnike.eyehealthmanager.domain.utils.toCalendar
import ru.rznnike.eyehealthmanager.domain.utils.toLocalDate
import java.time.temporal.ChronoUnit
import java.util.*

@InjectViewState
class AnalysisParametersPresenter : BasePresenter<AnalysisParametersView>() {
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
            parameters.apply {
                dateFrom = getTodayCalendar().apply {
                    add(Calendar.DAY_OF_MONTH, -GlobalConstants.ANALYSIS_MAX_RANGE_DAYS)
                }.timeInMillis
                dateTo = Calendar.getInstance().atEndOfDay().timeInMillis
                applyDynamicCorrections = getApplyDynamicCorrectionsUseCase().data ?: false
            }
            viewState.populateData(parameters)
        }
    }

    fun onDateFromValueChanged(newValue: Long) {
        parameters.dateFrom = newValue.toCalendar().atStartOfDay().timeInMillis
        val deltaDays = ChronoUnit.DAYS.between(parameters.dateFrom.toLocalDate(), parameters.dateTo.toLocalDate())
        when {
            deltaDays < GlobalConstants.ANALYSIS_MIN_RANGE_DAYS -> {
                parameters.dateTo = newValue.toCalendar()
                    .apply {
                        add(Calendar.DAY_OF_MONTH, GlobalConstants.ANALYSIS_MIN_RANGE_DAYS)
                    }
                    .atEndOfDay()
                    .timeInMillis
            }
            deltaDays > GlobalConstants.ANALYSIS_MAX_RANGE_DAYS -> {
                parameters.dateTo = newValue.toCalendar()
                    .apply {
                        add(Calendar.DAY_OF_MONTH, GlobalConstants.ANALYSIS_MAX_RANGE_DAYS)
                    }
                    .atEndOfDay()
                    .timeInMillis
            }
        }
        viewState.populateData(parameters)
    }

    fun onDateToValueChanged(newValue: Long) {
        parameters.dateTo = newValue.toCalendar().atEndOfDay().timeInMillis
        val deltaDays = ChronoUnit.DAYS.between(parameters.dateFrom.toLocalDate(), parameters.dateTo.toLocalDate())
        when {
            deltaDays < GlobalConstants.ANALYSIS_MIN_RANGE_DAYS -> {
                parameters.dateFrom = newValue.toCalendar()
                    .apply {
                        add(Calendar.DAY_OF_MONTH, -GlobalConstants.ANALYSIS_MIN_RANGE_DAYS)
                    }
                    .atStartOfDay()
                    .timeInMillis
            }
            deltaDays > GlobalConstants.ANALYSIS_MAX_RANGE_DAYS -> {
                parameters.dateFrom = newValue.toCalendar()
                    .apply {
                        add(Calendar.DAY_OF_MONTH, -GlobalConstants.ANALYSIS_MAX_RANGE_DAYS)
                    }
                    .atStartOfDay()
                    .timeInMillis
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