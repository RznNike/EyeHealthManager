package ru.rznnike.eyehealthmanager.app.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.rznnike.eyehealthmanager.app.navigation.FlowNavigationViewModel

internal val viewModelModule = module {
    viewModel { FlowNavigationViewModel(get()) }
}