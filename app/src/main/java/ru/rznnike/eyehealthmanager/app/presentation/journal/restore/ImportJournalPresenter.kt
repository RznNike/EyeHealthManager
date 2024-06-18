package ru.rznnike.eyehealthmanager.app.presentation.journal.restore

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.InjectViewState
import moxy.presenterScope
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.dispatcher.event.AppEvent
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.app.utils.JournalBackupManagerAndroid
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.interactor.test.ImportJournalUseCase
import ru.rznnike.eyehealthmanager.domain.model.test.TestType

@InjectViewState
class ImportJournalPresenter : BasePresenter<ImportJournalView>() {
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val dispatcherProvider: DispatcherProvider by inject()
    private val journalBackupManager: JournalBackupManagerAndroid by inject()
    private val importJournalUseCase: ImportJournalUseCase by inject()

    private var importFolderUri: Uri? = null
    private var availableImportTypes: List<TestType> = emptyList()
    private var startImportAutomatically = false

    override fun onFirstViewAttach() {
        populateData()
    }

    private fun populateData() = viewState.populateData(
        availableImportTypes = availableImportTypes,
        folderPath = importFolderUri?.lastPathSegment
    )

    fun onFolderSelected(uri: Uri, context: Context) {
        presenterScope.launch {
            importFolderUri = uri

            viewState.setProgress(true)
            withContext(dispatcherProvider.io) {
                journalBackupManager.context = context
                availableImportTypes = journalBackupManager.getAvailableImportTypes(uri)
                journalBackupManager.context = null
            }
            populateData()
            if (startImportAutomatically) {
                startImportAutomatically = false
                importFiles(context)
            }
            viewState.setProgress(false)
        }
    }

    fun importFiles(context: Context) {
        presenterScope.launch {
            when {
                importFolderUri == null -> {
                    startImportAutomatically = true
                    viewState.selectImportFolder()
                }
                availableImportTypes.isEmpty() -> {
                    notifier.sendMessage(R.string.error_no_backup_in_folder)
                }
                else -> {
                    viewState.setProgress(true)
                    importFolderUri?.let { importFolderUri ->
                        journalBackupManager.context = context
                        importJournalUseCase(
                            ImportJournalUseCase.Parameters(
                                importFolderUri = importFolderUri,
                                manager = journalBackupManager
                            )
                        ).process(
                            {
                                eventDispatcher.sendEvent(AppEvent.JournalImported(importFolderUri))
                                viewState.routerExit()
                            }, ::onError
                        )
                        journalBackupManager.context = null
                    }
                    viewState.setProgress(false)
                }
            }
        }
    }

    fun openImportFolder() {
        importFolderUri?.let {
            viewState.routerStartFlow(Screens.Common.actionOpenFolder(it))
        }
    }

    private fun onError(error: Throwable) =
        errorHandler.proceed(error) {
            notifier.sendMessage(it)
        }
}
