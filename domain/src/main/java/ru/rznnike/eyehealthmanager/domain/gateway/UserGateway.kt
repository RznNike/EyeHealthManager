package ru.rznnike.eyehealthmanager.domain.gateway

import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestingSettings
import ru.rznnike.eyehealthmanager.domain.model.test.TestingSettings
import ru.rznnike.eyehealthmanager.domain.model.common.AppTheme

interface UserGateway {
    suspend fun getWelcomeDialogShowed(): Boolean

    suspend fun setWelcomeDialogShowed(newValue: Boolean)

    suspend fun getDisplayedChangelogVersion(): Int

    suspend fun setDisplayedChangelogVersion(newValue: Int)

    suspend fun getTestingSettings(): TestingSettings

    suspend fun setTestingSettings(newValue: TestingSettings)

    suspend fun getAcuityTestingSettings(): AcuityTestingSettings

    suspend fun setAcuityTestingSettings(newValue: AcuityTestingSettings)

    suspend fun getApplyDynamicCorrections(): Boolean

    suspend fun setApplyDynamicCorrections(newValue: Boolean)

    suspend fun getAppTheme(): AppTheme

    suspend fun setAppTheme(appTheme: AppTheme)
}
