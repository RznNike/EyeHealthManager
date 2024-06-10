package ru.rznnike.eyehealthmanager.app.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import ru.rznnike.eyehealthmanager.app.crash.CrashlyticsProvider
import ru.rznnike.eyehealthmanager.app.crash.CrashlyticsProviderImpl
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.external.ExternalIntentDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.app.observer.AppLifeCycleObserver
import ru.rznnike.eyehealthmanager.app.notification.Notificator
import ru.rznnike.eyehealthmanager.domain.global.CoroutineScopeProvider
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import java.time.Clock

val appModule = module {
    factory { androidApplication().resources }

    factory { AppLifeCycleObserver() }
    single { Notifier(get()) }
    single { ErrorHandler(get(), get()) }
    single { EventDispatcher(get()) }
    single { ExternalIntentDispatcher(get()) }
    single { Notificator() }
    single<CrashlyticsProvider> { CrashlyticsProviderImpl() }
    single { Clock.systemUTC() }

    single<CoroutineScopeProvider> {
        object : CoroutineScopeProvider {
            override val ui = MainScope()
            override val default = CoroutineScope(Dispatchers.Default)
            override val io = CoroutineScope(Dispatchers.IO)
            override val unconfined = CoroutineScope(Dispatchers.Unconfined)
        }
    }

    single<DispatcherProvider> {
        object : DispatcherProvider {
            override val io = Dispatchers.IO
            override val default = Dispatchers.Default
            override val ui = Dispatchers.Main
            override val unconfined = Dispatchers.Unconfined
        }
    }
}
