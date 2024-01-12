package ru.rznnike.eyehealthmanager.app.presentation.journal.backup

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
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
import ru.rznnike.eyehealthmanager.domain.interactor.test.ExportJournalUseCase
import ru.rznnike.eyehealthmanager.domain.model.TestResultFilter
import ru.rznnike.eyehealthmanager.domain.model.enums.TestType
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.atEndOfDay
import ru.rznnike.eyehealthmanager.domain.utils.millis
import ru.rznnike.eyehealthmanager.domain.utils.toLocalDate
import java.time.Clock

@InjectViewState
class ExportJournalPresenter : BasePresenter<ExportJournalView>() {
    private val clock: Clock by inject()
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val context: Context by inject()
    private val exportJournalUseCase: ExportJournalUseCase by inject()

    private lateinit var filter: TestResultFilter
    private var startExportAutomatically = false

    override fun onFirstViewAttach() {
        initFilters()
    }

    private fun initFilters() {
        val dateNow = clock.millis().toLocalDate()
        filter = TestResultFilter(
            dateFrom = dateNow.minusMonths(1).atStartOfDay().millis(),
            dateTo = dateNow.atEndOfDay().millis()
        )
        populateData()
    }

    fun onClearFilters() {
        initFilters()
        populateData()
    }

    fun onFilterTestTypeClick(testType: TestType) {
        if (filter.selectedTestTypes.contains(testType)) {
            filter.selectedTestTypes.remove(testType)
        } else {
            filter.selectedTestTypes.add(testType)
        }
        filter.filterByType = filter.selectedTestTypes.isNotEmpty()
        populateData()
    }

    fun onFilterByDateValueChanged(value: Boolean) {
        filter.filterByDate = value
        populateData()
    }

    fun onFilterByTypeValueChanged(value: Boolean) {
        filter.filterByType = value
        populateData()
    }

    fun onFilterDateFromSelected(timestamp: Long) {
        filter.dateFrom = timestamp.toLocalDate().atStartOfDay().millis()
        if (filter.dateTo <= filter.dateFrom) {
            filter.dateTo = timestamp.toLocalDate().atEndOfDay().millis()
        }
        filter.filterByDate = true
        populateData()
    }

    fun onFilterDateToSelected(timestamp: Long) {
        filter.dateTo = timestamp.toLocalDate().atEndOfDay().millis()
        if (filter.dateTo <= filter.dateFrom) {
            filter.dateFrom = timestamp.toLocalDate().atStartOfDay().millis()
        }
        filter.filterByDate = true
        populateData()
    }

    private fun populateData() {
        val savedUri = getSavedExportFolder()
        val folderPath = savedUri?.let {
            "${savedUri.lastPathSegment}/${GlobalConstants.APP_DIR}/${GlobalConstants.EXPORT_DIR}"
        }
        viewState.populateData(filter, folderPath)
    }

    private fun getSavedExportFolder() =
        context.contentResolver.persistedUriPermissions.firstOrNull()?.uri

    fun onFolderSelected(newUri: Uri) {
        val previousUri = getSavedExportFolder()
        if (previousUri?.path != newUri.path) {
            previousUri?.let {
                context.contentResolver.releasePersistableUriPermission(previousUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.contentResolver.takePersistableUriPermission(newUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        populateData()

        if (startExportAutomatically) {
            startExportAutomatically = false
            startExport()
        }
    }

    fun startExport() {
        getSavedExportFolder()?.let {
            exportDatabase()
        } ?: run {
            startExportAutomatically = true
            viewState.selectExportFolder()
        }
    }

    private fun exportDatabase() {
        presenterScope.launch {
            viewState.setProgress(true)
            exportJournalUseCase(filter).process(
                { result ->
                    result.exportFolderUri?.let {
                        eventDispatcher.sendEvent(
                            AppEvent.JournalExported(it)
                        )
                    }
                    viewState.routerExit()
                }, { error ->
                    errorHandler.proceed(error) {
                        notifier.sendMessage(R.string.error_export)
                    }
                }
            )
            viewState.setProgress(false)
        }
    }

    fun openExportFolder() {
        fun DocumentFile.findOrCreateDocumentFolder(name: String) = findFile(name) ?: createDirectory(name)

        getSavedExportFolder()?.let { uri ->
            DocumentFile.fromTreeUri(context, uri)
                ?.findOrCreateDocumentFolder(GlobalConstants.APP_DIR)
                ?.findOrCreateDocumentFolder(GlobalConstants.EXPORT_DIR)
                ?.let { exportFolder ->
                    viewState.routerStartFlow(Screens.Common.actionOpenFolder(exportFolder.uri))
                }
        }
    }
}
