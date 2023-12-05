package ru.rznnike.eyehealthmanager.app.presentation.acuity.doctor

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView

interface AcuityDoctorResultView : NavigationMvpView {
    @AddToEndSingle
    fun setProgress(show: Boolean, immediately: Boolean = true)

    @AddToEndSingle
    fun populateData(date: Long?, leftEye: String, rightEye: String)
}
