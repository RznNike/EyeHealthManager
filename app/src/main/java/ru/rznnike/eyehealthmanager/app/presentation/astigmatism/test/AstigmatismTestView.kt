package ru.rznnike.eyehealthmanager.app.presentation.astigmatism.test

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView

interface AstigmatismTestView : NavigationMvpView {
    @AddToEndSingle
    fun setScale(
        dpmm: Float,
        distance: Int
    )
}
