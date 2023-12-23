package ru.rznnike.eyehealthmanager.app.presentation.colorperception.test

import androidx.annotation.ColorInt
import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView

interface ColorPerceptionTestView : NavigationMvpView {
    @AddToEndSingle
    fun setProgress(show: Boolean, immediately: Boolean = true)

    @AddToEndSingle
    fun populateData(@ColorInt color1: Int, @ColorInt color2: Int, progress: Int)
}
