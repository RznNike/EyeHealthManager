package ru.rznnike.eyehealthmanager.app.presentation.main.settings

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
import ru.rznnike.eyehealthmanager.app.utils.extensions.applyTheme
import ru.rznnike.eyehealthmanager.app.utils.extensions.getSelectedLanguage
import ru.rznnike.eyehealthmanager.app.utils.extensions.setSelectedLanguage
import ru.rznnike.eyehealthmanager.domain.interactor.dev.GenerateDataUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetAppThemeUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.SetAppThemeUseCase
import ru.rznnike.eyehealthmanager.domain.model.common.AppTheme
import ru.rznnike.eyehealthmanager.domain.model.common.DataGenerationType
import ru.rznnike.eyehealthmanager.domain.model.common.Language

@InjectViewState
class SettingsPresenter : BasePresenter<SettingsView>() {
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val getAppThemeUseCase: GetAppThemeUseCase by inject()
    private val setAppThemeUseCase: SetAppThemeUseCase by inject()
    private val generateDataUseCase: GenerateDataUseCase by inject()

    private var language = getSelectedLanguage()
    private var theme = AppTheme.SYSTEM

    override fun onFirstViewAttach() {
        initData()
    }

    fun onResume() {
        language = getSelectedLanguage()
        populateData()
    }

    private fun initData() {
        presenterScope.launch {
            getAppThemeUseCase().process(
                { result ->
                    theme = result
                }, ::onError
            )
            populateData()
        }
    }

    private fun populateData() =
        viewState.populateData(
            language = language,
            theme = theme
        )

    fun changeLanguage(language: Language) = setSelectedLanguage(language)

    fun changeTheme(newTheme: AppTheme) {
        presenterScope.launch {
            setAppThemeUseCase(newTheme).process(
                {
                    theme = newTheme
                    populateData()
                }, ::onError
            )
            applyTheme(newTheme)
        }
    }

    fun openTestingSettings() = viewState.routerNavigateTo(Screens.Screen.testingSettings())

    fun openAnalysis() = viewState.routerStartFlow(Screens.Flow.analysis())

    fun exportData() = viewState.routerNavigateTo(Screens.Screen.exportJournal())

    fun importData() = viewState.routerNavigateTo(Screens.Screen.importJournal())

    fun clearJournal() = eventDispatcher.sendEvent(AppEvent.JournalTotalDeletionRequested)

    fun deleteDuplicatesInJournal() =
        eventDispatcher.sendEvent(AppEvent.JournalDuplicatesDeletionRequested)

    fun generateData(type: DataGenerationType) {
        presenterScope.launch {
            viewState.setProgress(true)
            generateDataUseCase(type).process(
                { }, ::onError
            )
            eventDispatcher.sendEvent(AppEvent.JournalChanged)
            viewState.setProgress(false)
        }
    }

    private fun onError(error: Throwable) =
        errorHandler.proceed(error) {
            notifier.sendMessage(it)
        }
}
