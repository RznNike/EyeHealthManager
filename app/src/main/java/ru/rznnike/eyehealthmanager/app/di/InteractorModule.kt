package ru.rznnike.eyehealthmanager.app.di

import org.koin.dsl.module
import ru.rznnike.eyehealthmanager.domain.interactor.analysis.GetAnalysisResultUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.notification.EmitShowNotificationUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.notification.ObserveCancelNotificationUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.notification.ObserveShowNotificationUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.test.*
import ru.rznnike.eyehealthmanager.domain.interactor.user.ChangeUserLanguageUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetDisplayedChangelogVersionUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetWelcomeDialogShowedUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.SetDisplayedChangelogVersionUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.SetWelcomeDialogShowedUseCase

val interactorModule = module {
    single { GetWelcomeDialogShowedUseCase(get(), get()) }
    single { SetWelcomeDialogShowedUseCase(get(), get()) }
    single { GetDisplayedChangelogVersionUseCase(get(), get()) }
    single { SetDisplayedChangelogVersionUseCase(get(), get()) }
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
