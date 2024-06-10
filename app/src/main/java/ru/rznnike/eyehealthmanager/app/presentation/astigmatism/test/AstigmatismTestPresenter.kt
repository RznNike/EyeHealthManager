package ru.rznnike.eyehealthmanager.app.presentation.astigmatism.test

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.presenterScope
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.model.test.TestingSettings

@InjectViewState
class AstigmatismTestPresenter : BasePresenter<AstigmatismTestView>() {
    private val getTestingSettingsUseCase: GetTestingSettingsUseCase by inject()

    override fun onFirstViewAttach() {
        presenterScope.launch {
            val settings = getTestingSettingsUseCase().data ?: TestingSettings()
            viewState.setScale(settings)
        }
    }

    fun openAnswer() = viewState.routerNavigateTo(Screens.Screen.astigmatismAnswer())
}
