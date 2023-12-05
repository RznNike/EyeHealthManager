package ru.rznnike.eyehealthmanager.app.di

import com.github.terrakok.cicerone.Cicerone
import org.koin.dsl.module
import ru.rznnike.eyehealthmanager.app.navigation.AppRouter

val navigationModule = module {
    val cicerone: Cicerone<AppRouter> = Cicerone.create(AppRouter())
    single { cicerone.router }
    single { cicerone.getNavigatorHolder() }
}
