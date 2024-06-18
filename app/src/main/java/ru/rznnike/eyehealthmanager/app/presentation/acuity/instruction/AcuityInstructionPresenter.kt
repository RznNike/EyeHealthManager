package ru.rznnike.eyehealthmanager.app.presentation.acuity.instruction

import moxy.InjectViewState
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.domain.model.common.DayPart

@InjectViewState
class AcuityInstructionPresenter(
    private val dayPart: DayPart
) : BasePresenter<AcuityInstructionView>() {
    fun startTest() = viewState.routerNewRootScreen(Screens.Screen.acuityTest(dayPart))
}
