package ru.rznnike.eyehealthmanager.app.presentation.daltonism.result

import moxy.InjectViewState
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.domain.model.test.daltonism.DaltonismAnomalyType

@InjectViewState
class DaltonismResultPresenter(
    private val errorsCount: Int,
    private val resultType: DaltonismAnomalyType
) : BasePresenter<DaltonismResultView>() {
    override fun onFirstViewAttach() {
        viewState.populateData(
            errorsCount = errorsCount,
            resultType = resultType
        )
    }
}
