package ru.rznnike.eyehealthmanager.data.gateway

import ru.rznnike.eyehealthmanager.data.preference.PreferencesWrapper
import ru.rznnike.eyehealthmanager.domain.gateway.UserGateway
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestingSettings
import ru.rznnike.eyehealthmanager.domain.model.test.TestingSettings
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.common.AppTheme
import ru.rznnike.eyehealthmanager.domain.model.test.TestEyesType

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

    override suspend fun getTestingSettings() =
        preferences.run {
            TestingSettings(
                armsLength = armsLength.get(),
                dpmm = dotsPerMillimeter.get(),
                replaceBeginningWithMorning = replaceBeginningWithMorning.get(),
                enableAutoDayPart = enableAutoDayPart.get(),
                timeToDayBeginning = timeToDayBeginning.get(),
                timeToDayMiddle = timeToDayMiddle.get(),
                timeToDayEnd = timeToDayEnd.get()
            )
        }

    override suspend fun setTestingSettings(newValue: TestingSettings) {
        preferences.apply {
            armsLength.set(newValue.armsLength)
            dotsPerMillimeter.set(newValue.dpmm)
            replaceBeginningWithMorning.set(newValue.replaceBeginningWithMorning)
            enableAutoDayPart.set(newValue.enableAutoDayPart)
            timeToDayBeginning.set(newValue.timeToDayBeginning)
            timeToDayMiddle.set(newValue.timeToDayMiddle)
            timeToDayEnd.set(newValue.timeToDayEnd)
        }
    }

    override suspend fun getAcuityTestingSettings() =
        preferences.run {
            AcuityTestingSettings(
                symbolsType = AcuityTestSymbolsType[acuitySymbolsType.get()],
                eyesType = TestEyesType[testEyesType.get()]
            )
        }

    override suspend fun setAcuityTestingSettings(newValue: AcuityTestingSettings) {
        preferences.apply {
            acuitySymbolsType.set(newValue.symbolsType.id)
            testEyesType.set(newValue.eyesType.id)
        }
    }

    override suspend fun getApplyDynamicCorrections() =
        preferences.applyDynamicCorrections.get()

    override suspend fun setApplyDynamicCorrections(newValue: Boolean) =
        preferences.applyDynamicCorrections.set(newValue)

    override suspend fun getAppTheme() =
        AppTheme[preferences.appTheme.get()]

    override suspend fun setAppTheme(appTheme: AppTheme) =
        preferences.appTheme.set(appTheme.id)
}
