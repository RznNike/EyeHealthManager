package ru.rznnike.eyehealthmanager.app.di

import androidx.preference.PreferenceManager
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.rznnike.eyehealthmanager.data.preference.PreferencesWrapper

val preferenceModule = module {
    single { PreferencesWrapper(get()) }
    single { FlowSharedPreferences(get()) }
    factory { PreferenceManager.getDefaultSharedPreferences(androidContext()) }
}
