package ru.rznnike.eyehealthmanager.domain.gateway

import ru.rznnike.eyehealthmanager.domain.model.enums.Language

interface UserGateway {
    suspend fun getWelcomeDialogShowed(): Boolean

    suspend fun setWelcomeDialogShowed(newValue: Boolean)

    suspend fun getDisplayedChangelogVersion(): Int

    suspend fun setDisplayedChangelogVersion(newValue: Int)

    suspend fun changeLanguage(language: Language)
}
