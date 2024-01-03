package ru.rznnike.eyehealthmanager.app.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.rznnike.eyehealthmanager.data.storage.entity.MyObjectBox
import ru.rznnike.eyehealthmanager.domain.storage.repository.TestRepository
import ru.rznnike.eyehealthmanager.data.storage.repository.TestRepositoryImpl

val databaseModule = module {
    single { MyObjectBox.builder().androidContext(androidContext()).build() }
    single<TestRepository> { TestRepositoryImpl(get()) }
}
