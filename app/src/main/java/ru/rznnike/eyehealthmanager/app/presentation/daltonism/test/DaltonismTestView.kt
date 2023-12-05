package ru.rznnike.eyehealthmanager.app.presentation.daltonism.test

import androidx.annotation.DrawableRes
import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView

interface DaltonismTestView : NavigationMvpView {
    @AddToEndSingle
    fun setProgress(show: Boolean, immediately: Boolean = true)

    @AddToEndSingle
    fun populateData(@DrawableRes imageResId: Int, variants: List<Int>, progress: Int)
}
