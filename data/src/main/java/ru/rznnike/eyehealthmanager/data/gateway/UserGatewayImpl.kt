package ru.rznnike.eyehealthmanager.data.gateway

import ru.rznnike.eyehealthmanager.data.preference.PreferencesWrapper
import ru.rznnike.eyehealthmanager.domain.gateway.UserGateway
import ru.rznnike.eyehealthmanager.domain.model.enums.Language

class UserGatewayImpl(
    private val preferences: PreferencesWrapper
) : UserGateway {
    override suspend fun getWelcomeDialogShowed() =
        preferences.welcomeDialogShowed.get()

    override suspend fun setWelcomeDialogShowed(newValue: Boolean) =
        preferences.welcomeDialogShowed.set(newValue)

    override suspend fun getDisplayedChangelogVersion() =
        preferences.displayedChangelogVersion.get()

    override suspend fun setDisplayedChangelogVersion(newValue: Int) =
        preferences.displayedChangelogVersion.set(newValue)

    override suspend fun getLanguage() =
        Language[preferences.language.get()]

    override suspend fun setLanguage(newValue: Language) =
        preferences.language.set(newValue.tag)
}
