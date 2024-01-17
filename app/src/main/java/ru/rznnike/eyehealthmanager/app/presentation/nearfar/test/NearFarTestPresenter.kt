package ru.rznnike.eyehealthmanager.app.presentation.nearfar.test

import moxy.InjectViewState
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter

@InjectViewState
class NearFarTestPresenter : BasePresenter<NearFarTestView>() {
    fun openAnswerForm() = viewState.routerNavigateTo(Screens.Screen.nearFarAnswer())
}
