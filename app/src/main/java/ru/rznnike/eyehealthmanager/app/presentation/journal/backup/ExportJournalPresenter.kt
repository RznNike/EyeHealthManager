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
import ru.rznnike.eyehealthmanager.domain.model.TestResultFilterParams
import ru.rznnike.eyehealthmanager.domain.model.enums.TestType
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.atEndOfDay
import ru.rznnike.eyehealthmanager.domain.utils.atStartOfDay
import ru.rznnike.eyehealthmanager.domain.utils.getTodayCalendar
import ru.rznnike.eyehealthmanager.domain.utils.toCalendar
import java.util.Calendar

@InjectViewState
class ExportJournalPresenter : BasePresenter<ExportJournalView>() {
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val context: Context by inject()
    private val exportJournalUseCase: ExportJournalUseCase by inject()

    private lateinit var filterParams: TestResultFilterParams
    private var startExportAutomatically = false

    override fun onFirstViewAttach() {
        initFilters()
    }

    private fun initFilters() {
        filterParams = TestResultFilterParams(
            dateFrom = getTodayCalendar().apply {
                add(Calendar.MONTH, -1)
            }.timeInMillis,
            dateTo = Calendar.getInstance().atEndOfDay().timeInMillis
        )
        populateData()
    }

    fun onClearFilters() {
        initFilters()
        populateData()
    }

    fun onFilterTestTypeClick(testType: TestType) {
        if (filterParams.selectedTestTypes.contains(testType)) {
            filterParams.selectedTestTypes.remove(testType)
        } else {
            filterParams.selectedTestTypes.add(testType)
        }
        filterParams.filterByType = filterParams.selectedTestTypes.isNotEmpty()
        populateData()
    }

    fun onFilterByDateValueChanged(value: Boolean) {
        filterParams.filterByDate = value
        populateData()
    }

    fun onFilterByTypeValueChanged(value: Boolean) {
        filterParams.filterByType = value
        populateData()
    }

    fun onFilterDateFromSelected(timestamp: Long) {
        filterParams.dateFrom = timestamp.toCalendar().atStartOfDay().timeInMillis
        if (filterParams.dateTo <= filterParams.dateFrom) {
            filterParams.dateTo = timestamp.toCalendar().atEndOfDay().timeInMillis
        }
        filterParams.filterByDate = true
        populateData()
    }

    fun onFilterDateToSelected(timestamp: Long) {
        filterParams.dateTo = timestamp.toCalendar().atEndOfDay().timeInMillis
        if (filterParams.dateTo <= filterParams.dateFrom) {
            filterParams.dateFrom = timestamp.toCalendar().atStartOfDay().timeInMillis
        }
        filterParams.filterByDate = true
        populateData()
    }

    private fun populateData() {
        val savedUri = getSavedExportFolder()
        val folderPath = savedUri?.let {
            "${savedUri.lastPathSegment}/${GlobalConstants.APP_DIR}/${GlobalConstants.EXPORT_DIR}"
        }
        viewState.populateData(filterParams, folderPath)
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
            exportJournalUseCase(filterParams).process(
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
