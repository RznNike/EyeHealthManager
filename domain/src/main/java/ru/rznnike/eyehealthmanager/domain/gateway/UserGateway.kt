package ru.rznnike.eyehealthmanager.domain.gateway

import ru.rznnike.eyehealthmanager.domain.model.AcuityTestingSettings
import ru.rznnike.eyehealthmanager.domain.model.TestingSettings
import ru.rznnike.eyehealthmanager.domain.model.enums.Language

interface UserGateway {
    suspend fun getWelcomeDialogShowed(): Boolean

    suspend fun setWelcomeDialogShowed(newValue: Boolean)

    suspend fun getDisplayedChangelogVersion(): Int

    suspend fun setDisplayedChangelogVersion(newValue: Int)

    suspend fun getLanguage(): Language

    suspend fun setLanguage(newValue: Language)

    suspend fun getTestingSettings(): TestingSettings

    suspend fun setTestingSettings(newValue: TestingSettings)

    suspend fun getAcuityTestingSettings(): AcuityTestingSettings

    suspend fun setAcuityTestingSettings(newValue: AcuityTestingSettings)
}
