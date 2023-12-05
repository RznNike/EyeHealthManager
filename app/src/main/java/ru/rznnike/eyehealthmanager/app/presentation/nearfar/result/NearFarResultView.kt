package ru.rznnike.eyehealthmanager.app.presentation.nearfar.result

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView
import ru.rznnike.eyehealthmanager.domain.model.enums.NearFarAnswerType

interface NearFarResultView : NavigationMvpView {
    @AddToEndSingle
    fun populateData(answerLeftEye: NearFarAnswerType, answerRightEye: NearFarAnswerType)
}
