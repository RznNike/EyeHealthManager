package ru.rznnike.eyehealthmanager.data.gateway

import ru.rznnike.eyehealthmanager.data.preference.PreferencesWrapper
import ru.rznnike.eyehealthmanager.domain.gateway.UserGateway
import ru.rznnike.eyehealthmanager.domain.model.enums.Language

class UserGatewayImpl(
    private val preferences: PreferencesWrapper
) : UserGateway {
    override suspend fun changeLanguage(language: Language) =
        preferences.language.set(language.tag)
}
