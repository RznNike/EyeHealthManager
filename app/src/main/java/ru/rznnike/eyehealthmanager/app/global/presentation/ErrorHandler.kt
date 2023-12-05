package ru.rznnike.eyehealthmanager.app.global.presentation

import android.content.res.Resources
import android.util.Log
import kotlinx.coroutines.CancellationException
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.crash.CrashlyticsProvider
import ru.rznnike.eyehealthmanager.domain.model.exception.NotEnoughDataException
import java.io.FileNotFoundException

class ErrorHandler(
    var resources: Resources,
    private val crashlyticsProvider: CrashlyticsProvider
) {
    fun proceed(error: Throwable, callback: (String) -> Unit) {
        Log.e("ErrorHandler", error.message, error)
        crashlyticsProvider.recordException(error)
        when {
            error is CancellationException -> Unit
            else -> callback(getUserMessage(error))
        }
    }

    private fun getUserMessage(error: Throwable): String {
        val messageResId = when (error) {
            is FileNotFoundException -> R.string.error_file_not_found
            is NotEnoughDataException -> R.string.not_enough_data_exception
            is SecurityException -> R.string.security_error
            else -> R.string.unknown_error
        }
        return resources.getString(messageResId)
    }
}
