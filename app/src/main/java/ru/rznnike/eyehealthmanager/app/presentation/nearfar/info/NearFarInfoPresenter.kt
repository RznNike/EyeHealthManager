package ru.rznnike.eyehealthmanager.app.presentation.nearfar.info

import moxy.InjectViewState
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter

@InjectViewState
class NearFarInfoPresenter : BasePresenter<NearFarInfoView>() {
    fun startTest() = viewState.routerNewRootScreen(Screens.Screen.nearFarTest())
}
