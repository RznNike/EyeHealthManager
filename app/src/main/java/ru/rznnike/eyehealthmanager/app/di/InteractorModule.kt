package ru.rznnike.eyehealthmanager.app.di

import org.koin.dsl.module
import ru.rznnike.eyehealthmanager.domain.interactor.analysis.GetAnalysisResultUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.notification.EmitShowNotificationUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.notification.ObserveCancelNotificationUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.notification.ObserveShowNotificationUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.test.*
import ru.rznnike.eyehealthmanager.domain.interactor.user.ChangeUserLanguageUseCase

val interactorModule = module {
    single { ChangeUserLanguageUseCase(get(), get()) }

    single { GetTestResultsUseCase(get(), get()) }
    single { AddTestResultUseCase(get(), get()) }
    single { AddTestResultsUseCase(get(), get()) }
    single { DeleteTestResultUseCase(get(), get()) }
    single { DeleteAllTestResultsUseCase(get(), get()) }
    single { DeleteDuplicatesUseCase(get(), get()) }

    single { GetAnalysisResultUseCase(get(), get()) }

    single { EmitShowNotificationUseCase(get(), get()) }
    single { ObserveShowNotificationUseCase(get(), get()) }
    single { ObserveCancelNotificationUseCase(get(), get()) }
}
