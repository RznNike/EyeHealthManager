package ru.rznnike.eyehealthmanager.app.presentation.journal.restore

import android.net.Uri
import kotlinx.coroutines.launch
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
import ru.rznnike.eyehealthmanager.domain.interactor.test.GetAvailableImportTypesUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.test.ImportJournalUseCase
import ru.rznnike.eyehealthmanager.domain.model.test.TestType

@InjectViewState
class ImportJournalPresenter : BasePresenter<ImportJournalView>() {
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val getAvailableImportTypesUseCase: GetAvailableImportTypesUseCase by inject()
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

    fun onFolderSelected(uri: Uri) {
        presenterScope.launch {
            importFolderUri = uri

            viewState.setProgress(true)
            getAvailableImportTypesUseCase(uri).process(
                { result ->
                    availableImportTypes = result
                    populateData()
                    if (startImportAutomatically) {
                        startImportAutomatically = false
                        importFiles()
                    }
                }, ::onError
            )
            viewState.setProgress(false)
        }
    }

    fun importFiles() {
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
                        importJournalUseCase(importFolderUri).process(
                            {
                                eventDispatcher.sendEvent(AppEvent.JournalImported(importFolderUri))
                                viewState.routerExit()
                            }, ::onError
                        )
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
