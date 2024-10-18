package ru.rznnike.eyehealthmanager.app.presentation.main

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.presenterScope
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.BuildConfig
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dispatcher.event.AppEvent
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.domain.global.CoroutineScopeProvider
import ru.rznnike.eyehealthmanager.domain.interactor.test.DeleteAllTestResultsUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.test.DeleteDuplicatesUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetDisplayedChangelogVersionUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetWelcomeDialogShowedUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.SetDisplayedChangelogVersionUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.SetWelcomeDialogShowedUseCase

@InjectViewState
class MainPresenter : BasePresenter<MainView>(), EventDispatcher.EventListener {
    private val eventDispatcher: EventDispatcher by inject()
    private val notifier: Notifier by inject()
    private val errorHandler: ErrorHandler by inject()
    private val coroutineScopeProvider: CoroutineScopeProvider by inject()
    private val getWelcomeDialogShowedUseCase: GetWelcomeDialogShowedUseCase by inject()
    private val setWelcomeDialogShowedUseCase: SetWelcomeDialogShowedUseCase by inject()
    private val getDisplayedChangelogVersionUseCase: GetDisplayedChangelogVersionUseCase by inject()
    private val setDisplayedChangelogVersionUseCase: SetDisplayedChangelogVersionUseCase by inject()
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
            is AppEvent.JournalExported -> viewState.showSuccessfulExportDialog(event.uri)
            is AppEvent.JournalImported -> viewState.showSuccessfulImportDialog()
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
        presenterScope.launch {
            val showed = getWelcomeDialogShowedUseCase().data == true
            if (!showed) {
                viewState.showWelcomeDialog()
                setWelcomeDialogShowedUseCase(true)
            }
        }
    }

    private fun checkChangelog() {
        presenterScope.launch {
            val currentVersionCode = BuildConfig.VERSION_CODE
            val displayedVersionCode = getDisplayedChangelogVersionUseCase().data ?: currentVersionCode
            if (displayedVersionCode < currentVersionCode) {
                setDisplayedChangelogVersionUseCase(currentVersionCode)
                if (displayedVersionCode > 0) {
                    viewState.showChangelogDialog()
                }
            }
        }
    }

    fun deleteDuplicatesInJournal() {
        coroutineScopeProvider.io.launch {
            viewState.setProgress(true)
            deleteDuplicatesUseCase().process(
                {
                    notifier.sendAlert(R.string.duplicates_successfully_deleted)
                }, ::onError
            )
            eventDispatcher.sendEvent(AppEvent.JournalChanged)
            viewState.setProgress(false)
        }
    }

    private fun deleteJournal() {
        coroutineScopeProvider.io.launch {
            viewState.setProgress(true)
            deleteAllTestResultsUseCase().process(
                {
                    notifier.sendAlert(R.string.clear_journal_success)
                }, ::onError
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