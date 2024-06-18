package ru.rznnike.eyehealthmanager.app.observer

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.app.service.NotificationService

class AppLifeCycleObserver : DefaultLifecycleObserver, KoinComponent {
    private val context: Context by inject()
    private val errorHandler: ErrorHandler by inject()

    override fun onResume(owner: LifecycleOwner) {
        try {
            NotificationService.startAsJob(context)
        } catch (exception: IllegalStateException) {
            errorHandler.proceed(exception) {}
        }
    }
}