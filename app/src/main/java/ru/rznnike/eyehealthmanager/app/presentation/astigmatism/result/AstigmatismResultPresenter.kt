package ru.rznnike.eyehealthmanager.app.presentation.astigmatism.result

import moxy.InjectViewState
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.domain.model.enums.AstigmatismAnswerType

@InjectViewState
class AstigmatismResultPresenter(
    private val answerLeftEye: AstigmatismAnswerType,
    private val answerRightEye: AstigmatismAnswerType
) : BasePresenter<AstigmatismResultView>() {
    override fun onFirstViewAttach() {
        viewState.populateData(
            answerLeftEye = answerLeftEye,
            answerRightEye = answerRightEye
        )
    }
}
