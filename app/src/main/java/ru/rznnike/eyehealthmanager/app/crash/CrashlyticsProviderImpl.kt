package ru.rznnike.eyehealthmanager.app.crash

import com.google.firebase.crashlytics.FirebaseCrashlytics

class CrashlyticsProviderImpl : CrashlyticsProvider {
    override fun recordException(throwable: Throwable) =
        FirebaseCrashlytics.getInstance().recordException(throwable)

    override fun setCustomKey(key: String, value: String) =
        FirebaseCrashlytics.getInstance().setCustomKey(key, value)
}