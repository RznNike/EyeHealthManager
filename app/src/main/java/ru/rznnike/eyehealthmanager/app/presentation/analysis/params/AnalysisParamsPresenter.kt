package ru.rznnike.eyehealthmanager.app.presentation.analysis.params

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.presenterScope
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.data.preference.PreferencesWrapper
import ru.rznnike.eyehealthmanager.domain.model.AnalysisParams
import ru.rznnike.eyehealthmanager.domain.model.enums.AnalysisType
import java.util.*

@InjectViewState
class AnalysisParamsPresenter : BasePresenter<AnalysisParamsView>() {
    private val preferences: PreferencesWrapper by inject()

    private val params = AnalysisParams()

    override fun onFirstViewAttach() {
        presenterScope.launch {
            initParams()
            viewState.populateData(params)
        }
    }

    private fun initParams() {
        params.dateFrom = Calendar.getInstance()
            .apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.MONTH, -3)
            }
            .timeInMillis
        params.dateTo = Calendar.getInstance()
            .apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            .timeInMillis
        params.applyDynamicCorrections = preferences.applyDynamicCorrectionsInAnalysis.get()
    }

    fun onDateFromValueChanged(timestamp: Long) {
        params.dateFrom = timestamp
        if (params.dateTo <= params.dateFrom) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = timestamp
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            params.dateTo = calendar.timeInMillis
        }
        viewState.populateData(params)
    }

    fun onDateToValueChanged(timestamp: Long) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        params.dateTo = calendar.timeInMillis
        if (params.dateTo <= params.dateFrom) {
            calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            params.dateFrom = calendar.timeInMillis
        }
        viewState.populateData(params)
    }

    fun onAnalysisTypeValueChanged(analysisType: AnalysisType) {
        params.analysisType = analysisType
        viewState.populateData(params)
    }

    fun onCheckBoxApplyDynamicCorrectionsClicked(checked: Boolean) {
        params.applyDynamicCorrections = checked
        preferences.applyDynamicCorrectionsInAnalysis.set(checked)
        viewState.populateData(params)
    }

    fun onStartAnalysis() {
        viewState.routerNewRootScreen(Screens.Screen.analysisResult(params))
    }
}