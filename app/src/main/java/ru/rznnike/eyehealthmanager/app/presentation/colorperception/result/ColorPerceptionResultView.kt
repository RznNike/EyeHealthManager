package ru.rznnike.eyehealthmanager.app.presentation.colorperception.result

import androidx.annotation.StringRes
import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView

interface ColorPerceptionResultView : NavigationMvpView {
    @AddToEndSingle
    fun populateData(recognizedCount: Int, allCount: Int, @StringRes messageResId: Int)
}
