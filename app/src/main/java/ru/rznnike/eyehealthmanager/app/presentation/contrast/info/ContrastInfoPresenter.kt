package ru.rznnike.eyehealthmanager.app.presentation.contrast.info

import moxy.InjectViewState
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter

@InjectViewState
class ContrastInfoPresenter : BasePresenter<ContrastInfoView>() {
    fun onStart() {
        viewState.routerNewRootScreen(Screens.Screen.contrastTest())
    }
}
