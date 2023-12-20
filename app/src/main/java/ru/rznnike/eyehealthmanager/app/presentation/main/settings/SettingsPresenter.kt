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
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetUserLanguageUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.SetUserLanguageUseCase
import ru.rznnike.eyehealthmanager.domain.model.enums.Language

@InjectViewState
class SettingsPresenter : BasePresenter<SettingsView>() {
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val getUserLanguageUseCase: GetUserLanguageUseCase by inject()
    private val setUserLanguageUseCase: SetUserLanguageUseCase by inject()

    fun onResume() {
        presenterScope.launch {
            getUserLanguageUseCase().process(
                {
                    viewState.populateData(it)
                }
            )
        }
    }

    fun changeLanguage(language: Language) {
        presenterScope.launch {
            viewState.setProgress(true)
            setUserLanguageUseCase(language).process(
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

    fun openTestingSettings() = viewState.routerNavigateTo(Screens.Screen.testingSettings())

    fun openAnalysis() = viewState.routerStartFlow(Screens.Flow.analysis())

    fun exportData() = viewState.routerNavigateTo(Screens.Screen.exportJournal())

    fun importData() = viewState.routerNavigateTo(Screens.Screen.importJournal())

    fun clearJournal() = eventDispatcher.sendEvent(AppEvent.JournalTotalDeletionRequested)

    fun deleteDuplicatesInJournal() =
        eventDispatcher.sendEvent(AppEvent.JournalDuplicatesDeletionRequested)
}
