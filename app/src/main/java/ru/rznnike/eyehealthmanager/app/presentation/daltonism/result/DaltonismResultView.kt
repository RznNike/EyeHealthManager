package ru.rznnike.eyehealthmanager.app.presentation.daltonism.result

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView
import ru.rznnike.eyehealthmanager.domain.model.test.daltonism.DaltonismAnomalyType

interface DaltonismResultView : NavigationMvpView {
    @AddToEndSingle
    fun populateData(errorsCount: Int, resultType: DaltonismAnomalyType)
}
