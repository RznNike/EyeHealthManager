package ru.rznnike.eyehealthmanager.app.presentation.main

import kotlinx.coroutines.launch
import moxy.InjectViewState
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.BuildConfig
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dispatcher.event.AppEvent
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.data.preference.PreferencesWrapper
import ru.rznnike.eyehealthmanager.domain.global.CoroutineProvider
import ru.rznnike.eyehealthmanager.domain.interactor.test.DeleteAllTestResultsUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.test.DeleteDuplicatesUseCase

@InjectViewState
class MainPresenter : BasePresenter<MainView>(), EventDispatcher.EventListener {
    private val eventDispatcher: EventDispatcher by inject()
    private val notifier: Notifier by inject()
    private val errorHandler: ErrorHandler by inject()
    private val coroutineProvider: CoroutineProvider by inject()
    private val preferences: PreferencesWrapper by inject()
    private val deleteDuplicatesUseCase: DeleteDuplicatesUseCase by inject()
    private val deleteAllTestResultsUseCase: DeleteAllTestResultsUseCase by inject()

    var selectedNavigationTab: MainView.NavigationTab = MainView.NavigationTab.TESTS

    init {
        subscribeToEvents()
    }

    override fun onDestroy() {
        eventDispatcher.removeEventListener(this)
        super.onDestroy()
    }

    private fun subscribeToEvents() {
        eventDispatcher.addEventListener(AppEvent.JournalExported::class, this)
        eventDispatcher.addEventListener(AppEvent.JournalImported::class, this)
        eventDispatcher.addEventListener(AppEvent.JournalDuplicatesDeletionRequested::class, this)
        eventDispatcher.addEventListener(AppEvent.JournalTotalDeletionRequested::class, this)
    }

    override fun onEvent(event: AppEvent) {
        when (event) {
            is AppEvent.JournalExported -> {
                viewState.showSuccessfulExportDialog(event.uri)
            }
            is AppEvent.JournalImported -> {
                viewState.showSuccessfulImportDialog(event.uri)
            }
            is AppEvent.JournalDuplicatesDeletionRequested -> deleteDuplicatesInJournal()
            is AppEvent.JournalTotalDeletionRequested -> deleteJournal()
            else -> Unit
        }
    }

    override fun onFirstViewAttach() {
        checkWelcomeDialog()
        checkChangelog()
    }

    private fun checkWelcomeDialog() {
        if (!preferences.welcomeDialogShowed.get()) {
            viewState.showWelcomeDialog()
            preferences.welcomeDialogShowed.set(true)
        }
    }

    private fun checkChangelog() {
        val currentVersionCode = BuildConfig.VERSION_CODE
        val displayedVersionCode = preferences.displayedChangelogVersion.get()
        when {
            displayedVersionCode < 0 -> {
                preferences.displayedChangelogVersion.set(currentVersionCode)
            }
            displayedVersionCode != currentVersionCode -> {
                viewState.showChangelogDialog()
                preferences.displayedChangelogVersion.set(currentVersionCode)
            }
        }
    }

    fun deleteDuplicatesInJournal() {
        coroutineProvider.scopeIo.launch {
            viewState.setProgress(true)
            deleteDuplicatesUseCase().process(
                {
                    notifier.sendAlert(R.string.duplicates_successfully_deleted)
                }, { error ->
                    errorHandler.proceed(error) {
                        notifier.sendMessage(it)
                    }
                }
            )

            eventDispatcher.sendEvent(AppEvent.JournalChanged)
            viewState.setProgress(false)
        }
    }

    private fun deleteJournal() {
        coroutineProvider.scopeIo.launch {
            viewState.setProgress(true)
            deleteAllTestResultsUseCase().process(
                {
                    notifier.sendMessage(R.string.clear_journal_success)
                }, { error ->
                    errorHandler.proceed(error) {
                        notifier.sendMessage(it)
                    }
                }
            )

            eventDispatcher.sendEvent(AppEvent.JournalChanged)
            viewState.setProgress(false)
        }
    }
}