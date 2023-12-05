package ru.rznnike.eyehealthmanager.app.presentation.astigmatism.test

import moxy.InjectViewState
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.data.preference.PreferencesWrapper

@InjectViewState
class AstigmatismTestPresenter : BasePresenter<AstigmatismTestView>() {
    private val preferences: PreferencesWrapper by inject()

    override fun onFirstViewAttach() {
        viewState.setScale(
            dpmm = preferences.dotsPerMillimeter.get(),
            distance = preferences.armsLength.get()
        )
    }

    fun onNext() {
        viewState.routerNavigateTo(Screens.Screen.astigmatismAnswer())
    }
}
