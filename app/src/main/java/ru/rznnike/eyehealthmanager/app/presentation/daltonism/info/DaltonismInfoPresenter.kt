package ru.rznnike.eyehealthmanager.app.presentation.daltonism.info

import moxy.InjectViewState
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter

@InjectViewState
class DaltonismInfoPresenter : BasePresenter<DaltonismInfoView>() {
    fun startTest() = viewState.routerNewRootScreen(Screens.Screen.daltonismTest())
}
