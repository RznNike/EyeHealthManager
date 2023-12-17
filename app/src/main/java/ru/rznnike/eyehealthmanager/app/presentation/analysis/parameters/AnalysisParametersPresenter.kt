package ru.rznnike.eyehealthmanager.app.presentation.analysis.parameters

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.presenterScope
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetApplyDynamicCorrectionsUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.SetApplyDynamicCorrectionsUseCase
import ru.rznnike.eyehealthmanager.domain.model.AnalysisParameters
import ru.rznnike.eyehealthmanager.domain.model.enums.AnalysisType
import ru.rznnike.eyehealthmanager.domain.utils.atEndOfDay
import ru.rznnike.eyehealthmanager.domain.utils.atStartOfDay
import ru.rznnike.eyehealthmanager.domain.utils.getTodayCalendar
import ru.rznnike.eyehealthmanager.domain.utils.toCalendar
import java.util.*

@InjectViewState
class AnalysisParametersPresenter : BasePresenter<AnalysisParametersView>() {
    private val getApplyDynamicCorrectionsUseCase: GetApplyDynamicCorrectionsUseCase by inject()
    private val setApplyDynamicCorrectionsUseCase: SetApplyDynamicCorrectionsUseCase by inject()

    private val parameters = AnalysisParameters()

    override fun onFirstViewAttach() {
        initData()
    }

    private fun initData() {
        presenterScope.launch {
            parameters.apply {
                dateFrom = getTodayCalendar().apply {
                    add(Calendar.MONTH, -3)
                }.timeInMillis
                dateTo = Calendar.getInstance().atEndOfDay().timeInMillis
                applyDynamicCorrections = getApplyDynamicCorrectionsUseCase().data ?: false
            }
            viewState.populateData(parameters)
        }
    }

    fun onDateFromValueChanged(newValue: Long) {
        parameters.dateFrom = newValue.toCalendar().atStartOfDay().timeInMillis
        if (parameters.dateTo <= parameters.dateFrom) {
            parameters.dateTo = newValue.toCalendar().atEndOfDay().timeInMillis
        }
        viewState.populateData(parameters)
    }

    fun onDateToValueChanged(newValue: Long) {
        parameters.dateTo = newValue.toCalendar().atEndOfDay().timeInMillis
        if (parameters.dateTo <= parameters.dateFrom) {
            parameters.dateFrom = newValue.toCalendar().atStartOfDay().timeInMillis
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

    fun startAnalysis() = viewState.routerNewRootScreen(Screens.Screen.analysisResult(parameters))
}