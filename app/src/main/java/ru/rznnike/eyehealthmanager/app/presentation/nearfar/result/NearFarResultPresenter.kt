package ru.rznnike.eyehealthmanager.app.presentation.nearfar.result

import moxy.InjectViewState
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.domain.model.test.nearfar.NearFarAnswerType

@InjectViewState
class NearFarResultPresenter(
    private val answerLeftEye: NearFarAnswerType,
    private val answerRightEye: NearFarAnswerType
) : BasePresenter<NearFarResultView>() {
    override fun onFirstViewAttach() {
        viewState.populateData(answerLeftEye, answerRightEye)
    }
}
