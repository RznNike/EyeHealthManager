package ru.rznnike.eyehealthmanager.domain.gateway

import ru.rznnike.eyehealthmanager.domain.model.enums.Language

interface UserGateway {
    suspend fun changeLanguage(language: Language)
}
