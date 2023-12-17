package ru.rznnike.eyehealthmanager.app.presentation.astigmatism.test

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView
import ru.rznnike.eyehealthmanager.domain.model.TestingSettings

interface AstigmatismTestView : NavigationMvpView {
    @AddToEndSingle
    fun setScale(settings: TestingSettings)
}
