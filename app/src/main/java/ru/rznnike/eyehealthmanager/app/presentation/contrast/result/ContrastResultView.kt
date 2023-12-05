package ru.rznnike.eyehealthmanager.app.presentation.contrast.result

import androidx.annotation.StringRes
import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView

interface ContrastResultView : NavigationMvpView {
    @AddToEndSingle
    fun populateData(recognizedDelta: Int, @StringRes messageResId: Int)
}
