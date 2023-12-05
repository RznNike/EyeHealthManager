package ru.rznnike.eyehealthmanager.app.di

val appComponent = listOf(
    appModule,
    databaseModule,
    gatewayModule,
    navigationModule,
    preferenceModule,
    interactorModule,
    viewModelModule
)