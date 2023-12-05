package ru.rznnike.eyehealthmanager.app.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.rznnike.eyehealthmanager.app.crash.CrashlyticsProvider
import ru.rznnike.eyehealthmanager.app.crash.CrashlyticsProviderImpl
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.external.ExternalIntentDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.app.observer.AppLifeCycleObserver
import ru.rznnike.eyehealthmanager.device.notification.Notificator
import ru.rznnike.eyehealthmanager.domain.global.CoroutineProvider
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider

val appModule = module {
    factory { androidContext().resources }

    factory { AppLifeCycleObserver() }
    single { Notifier(get()) }
    single { ErrorHandler(get(), get()) }
    single { EventDispatcher() }
    single { ExternalIntentDispatcher(get()) }
    single { Notificator(androidContext()) }
    single<CrashlyticsProvider> { CrashlyticsProviderImpl() }

    single<CoroutineProvider> {
        object : CoroutineProvider {
            override val scopeIo = CoroutineScope(Dispatchers.IO)
            override val scopeMain = MainScope()
            override val scopeMainImmediate = CoroutineScope(Dispatchers.Main.immediate)
            override val scopeUnconfined = CoroutineScope(Dispatchers.Unconfined)
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
