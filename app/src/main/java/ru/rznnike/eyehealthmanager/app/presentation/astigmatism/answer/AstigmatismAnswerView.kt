package ru.rznnike.eyehealthmanager.app.presentation.astigmatism.answer

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView

interface AstigmatismAnswerView : NavigationMvpView {
    @AddToEndSingle
    fun setProgress(show: Boolean, immediately: Boolean = true)
}
