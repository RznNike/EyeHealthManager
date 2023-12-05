package ru.rznnike.eyehealthmanager.app.presentation.main.settings

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.presenterScope
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.dispatcher.event.AppEvent
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.data.preference.PreferencesWrapper
import ru.rznnike.eyehealthmanager.domain.interactor.user.ChangeUserLanguageUseCase
import ru.rznnike.eyehealthmanager.domain.model.enums.Language

@InjectViewState
class SettingsPresenter : BasePresenter<SettingsView>() {
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val preferences: PreferencesWrapper by inject()
    private val changeUserLanguageUseCase: ChangeUserLanguageUseCase by inject()

    fun onResume() {
        viewState.populateData(
            currentLanguage = Language[preferences.language.get()]
        )
    }

    fun changeLanguage(language: Language) {
        presenterScope.launch {
            viewState.setProgress(true)
            changeUserLanguageUseCase(language).process(
                {
                    delay(500)
                    viewState.updateUiLanguage()
                }, { error ->
                    errorHandler.proceed(error) {
                        notifier.sendMessage(it)
                    }
                }
            )
            viewState.setProgress(true)
        }
    }

    fun openTestingSettings() {
        viewState.routerNavigateTo(Screens.Screen.testingSettings())
    }

    fun exportData() {
        viewState.routerNavigateTo(Screens.Screen.exportJournal())
    }

    fun importData() {
        viewState.routerNavigateTo(Screens.Screen.importJournal())
    }

    fun clearJournal() {
        eventDispatcher.sendEvent(AppEvent.JournalTotalDeletionRequested)
    }

    fun deleteDuplicatesInJournal() {
        eventDispatcher.sendEvent(AppEvent.JournalDuplicatesDeletionRequested)
    }
}
