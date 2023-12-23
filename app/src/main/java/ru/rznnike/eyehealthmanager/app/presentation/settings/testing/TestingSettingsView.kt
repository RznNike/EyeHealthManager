package ru.rznnike.eyehealthmanager.app.presentation.settings.testing

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView
import ru.rznnike.eyehealthmanager.domain.model.TestingSettings

interface TestingSettingsView : NavigationMvpView {
    @AddToEndSingle
    fun populateData(settings: TestingSettings)
}
