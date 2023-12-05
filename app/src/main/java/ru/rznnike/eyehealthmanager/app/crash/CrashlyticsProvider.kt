package ru.rznnike.eyehealthmanager.app.crash

interface CrashlyticsProvider {
    fun recordException(throwable: Throwable)

    fun setCustomKey(key: String, value: String)
}