package ru.rznnike.eyehealthmanager.app.presentation.main.settings

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView
import ru.rznnike.eyehealthmanager.domain.model.common.AppTheme
import ru.rznnike.eyehealthmanager.domain.model.common.Language

interface SettingsView : NavigationMvpView {
    @AddToEndSingle
    fun setProgress(show: Boolean, immediately: Boolean = true)

    @AddToEndSingle
    fun populateData(language: Language, theme: AppTheme)
}
