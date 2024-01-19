package ru.rznnike.eyehealthmanager.app.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.rznnike.eyehealthmanager.data.storage.dao.AcuityTestDAO
import ru.rznnike.eyehealthmanager.data.storage.dao.AstigmatismTestDAO
import ru.rznnike.eyehealthmanager.data.storage.dao.ColorPerceptionTestDAO
import ru.rznnike.eyehealthmanager.data.storage.dao.ContrastTestDAO
import ru.rznnike.eyehealthmanager.data.storage.dao.DaltonismTestDAO
import ru.rznnike.eyehealthmanager.data.storage.dao.NearFarTestDAO
import ru.rznnike.eyehealthmanager.data.storage.dao.TestDAO
import ru.rznnike.eyehealthmanager.data.storage.entity.MyObjectBox
import ru.rznnike.eyehealthmanager.domain.storage.repository.TestRepository
import ru.rznnike.eyehealthmanager.data.storage.repository.TestRepositoryImpl

val databaseModule = module {
    single { MyObjectBox.builder().androidContext(androidContext()).build() }
    single { TestDAO(get()) }
    single { AcuityTestDAO(get()) }
    single { AstigmatismTestDAO(get()) }
    single { ColorPerceptionTestDAO(get()) }
    single { ContrastTestDAO(get()) }
    single { DaltonismTestDAO(get()) }
    single { NearFarTestDAO(get()) }
    single<TestRepository> { TestRepositoryImpl(get(), get(), get(), get(), get(), get(), get(), get()) }
}
