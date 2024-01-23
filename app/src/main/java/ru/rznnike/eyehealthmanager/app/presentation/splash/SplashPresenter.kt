package ru.rznnike.eyehealthmanager.app.presentation.splash

import moxy.InjectViewState
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter

@InjectViewState
class SplashPresenter : BasePresenter<SplashView>() {
    fun onAnimationEnd() = viewState.routerNewRootFlow(Screens.Flow.main())
}
