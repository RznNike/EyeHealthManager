package ru.rznnike.eyehealthmanager.app.presentation.colorperception.info

import moxy.InjectViewState
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter

@InjectViewState
class ColorPerceptionInfoPresenter : BasePresenter<ColorPerceptionInfoView>() {
    fun onStart() {
        viewState.routerNewRootScreen(Screens.Screen.colorPerceptionTest())
    }

    fun onDaltonismTest() {
        viewState.routerReplaceFlow(Screens.Flow.daltonism())
    }
}
