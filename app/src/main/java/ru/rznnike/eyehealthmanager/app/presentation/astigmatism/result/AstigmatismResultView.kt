package ru.rznnike.eyehealthmanager.app.presentation.astigmatism.result

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView
import ru.rznnike.eyehealthmanager.domain.model.enums.AstigmatismAnswerType

interface AstigmatismResultView : NavigationMvpView {
    @AddToEndSingle
    fun populateData(answerLeftEye: AstigmatismAnswerType, answerRightEye: AstigmatismAnswerType)
}
