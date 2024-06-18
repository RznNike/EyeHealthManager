package ru.rznnike.eyehealthmanager.app.presentation.contrast.test

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView
import ru.rznnike.eyehealthmanager.domain.model.common.Direction

interface ContrastTestView : NavigationMvpView {
    @AddToEndSingle
    fun setProgress(show: Boolean, immediately: Boolean = true)

    @AddToEndSingle
    fun populateData(
        direction: Direction,
        backgroundAlpha: Float,
        foregroundDelta: Float,
        progress: Int
    )
}
