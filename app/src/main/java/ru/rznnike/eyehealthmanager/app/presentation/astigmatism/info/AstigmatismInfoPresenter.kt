package ru.rznnike.eyehealthmanager.app.presentation.astigmatism.info

import moxy.InjectViewState
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter

@InjectViewState
class AstigmatismInfoPresenter : BasePresenter<AstigmatismInfoView>() {
    fun startTest() = viewState.routerNewRootScreen(Screens.Screen.astigmatismTest())
}
