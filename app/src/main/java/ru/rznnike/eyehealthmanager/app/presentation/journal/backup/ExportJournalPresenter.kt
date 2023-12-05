package ru.rznnike.eyehealthmanager.app.presentation.journal.backup

import android.annotation.SuppressLint
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
import ru.rznnike.eyehealthmanager.domain.interactor.test.GetTestResultsUseCase
import ru.rznnike.eyehealthmanager.domain.model.*
import ru.rznnike.eyehealthmanager.domain.model.enums.TestType
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.toDate
import java.io.BufferedWriter
import java.io.IOException
import java.util.*

private const val EXPORT_PAGE_SIZE = 100
private const val EXPORT_DIR = "export"

@InjectViewState
class ExportJournalPresenter : BasePresenter<ExportJournalView>() {
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val context: Context by inject()
    private val getTestResultsUseCase: GetTestResultsUseCase by inject()

    private val filterParams = TestResultFilterParams()
    private val files: MutableMap<TestType, DocumentFile> = EnumMap(TestType::class.java)
    private val fileWriters: MutableMap<TestType, BufferedWriter> = EnumMap(TestType::class.java)
    private val entryCounters: MutableMap<TestType, Int> = EnumMap(TestType::class.java)
    private var exportFolderUri: Uri? = null
    private var startExportAutomatically = false

    override fun onFirstViewAttach() {
        initFilters()
    }

    private fun initFilters() {
        filterParams.dateFrom = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MONTH, -1)
        }.timeInMillis
        filterParams.dateTo = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
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
        filterParams.dateFrom = timestamp
        if (filterParams.dateTo <= filterParams.dateFrom) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = timestamp
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            filterParams.dateTo = calendar.timeInMillis
        }
        filterParams.filterByDate = true
        populateData()
    }

    fun onFilterDateToSelected(timestamp: Long) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        filterParams.dateTo = calendar.timeInMillis
        if (filterParams.dateTo <= filterParams.dateFrom) {
            calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            filterParams.dateFrom = calendar.timeInMillis
        }
        filterParams.filterByDate = true
        populateData()
    }

    private fun populateData() {
        val savedUri = getSavedExportFolder()
        val folderPath = savedUri?.let {
            "${savedUri.lastPathSegment}/${context.resources.getString(R.string.app_name)}/$EXPORT_DIR"
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

    @SuppressLint("Recycle")
    private fun exportDatabase() {
        presenterScope.launch {
            getSavedExportFolder()?.let { uri ->
                viewState.setProgress(true)
                DocumentFile.fromTreeUri(context, uri)
                    ?.findOrCreateDocumentFolder(context.getString(R.string.app_name))
                    ?.findOrCreateDocumentFolder(EXPORT_DIR)
                    ?.findOrCreateDocumentFolder(
                        System.currentTimeMillis().toDate(GlobalConstants.DATE_PATTERN_FULL_FOR_PATH)
                    )
                    ?.let { folder ->
                        exportFolderUri = folder.uri
                        TestType.values().forEach { type ->
                            val fileName = type.toString().lowercase()
                            folder.createFile(
                                "text/tab-separated-values",
                                "${fileName}.tsv"
                            )?.let { file ->
                                try {
                                    val header = when (type) {
                                        TestType.ACUITY -> AcuityTestResult.EXPORT_HEADER
                                        TestType.ASTIGMATISM -> AstigmatismTestResult.EXPORT_HEADER
                                        TestType.NEAR_FAR -> NearFarTestResult.EXPORT_HEADER
                                        TestType.COLOR_PERCEPTION -> ColorPerceptionTestResult.EXPORT_HEADER
                                        TestType.DALTONISM -> DaltonismTestResult.EXPORT_HEADER
                                        TestType.CONTRAST -> ContrastTestResult.EXPORT_HEADER
                                    }
                                    context.contentResolver.openOutputStream(file.uri)?.let { outputStream ->
                                        outputStream.bufferedWriter().let {
                                            fileWriters[type] = it
                                            it.appendLine(header)
                                        }
                                    }
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }

                                files[type] = file
                                entryCounters[type] = 0
                            }
                        }
                        exportDatabasePageToFiles(0)
                    }
            }
        }
    }

    private fun DocumentFile.findOrCreateDocumentFolder(name: String) =
        findFile(name) ?: createDirectory(name)

    private fun exportDatabasePageToFiles(pageOffset: Int) {
        presenterScope.launch {
            getTestResultsUseCase(TestResultPagingParams(EXPORT_PAGE_SIZE, pageOffset, filterParams)).process(
                { results ->
                    results.forEach { testResult ->
                        when (testResult) {
                            is AcuityTestResult -> TestType.ACUITY
                            is AstigmatismTestResult -> TestType.ASTIGMATISM
                            is ColorPerceptionTestResult -> TestType.COLOR_PERCEPTION
                            is ContrastTestResult -> TestType.CONTRAST
                            is DaltonismTestResult -> TestType.DALTONISM
                            is NearFarTestResult -> TestType.NEAR_FAR
                            else -> null
                        }?.let { type ->
                            val exportString = testResult.exportToString()
                            fileWriters[type]?.appendLine(exportString)
                            entryCounters[type] = (entryCounters[type] ?: 0) + 1
                        }
                    }
                    fileWriters.values.forEach {
                        it.flush()
                    }
                    if (results.size < EXPORT_PAGE_SIZE) {
                        finishExport()
                    } else {
                        exportDatabasePageToFiles(pageOffset + EXPORT_PAGE_SIZE)
                    }
                }, { error ->
                    errorHandler.proceed(error) {
                        notifier.sendMessage(it)
                    }
                    finishExport(isError = true)
                }
            )
        }
    }

    private fun finishExport(isError: Boolean = false) {
        fileWriters.forEach {
            try {
                it.value.close()
                if (entryCounters[it.key] == 0) {
                    files[it.key]?.delete()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        viewState.setProgress(false)
        if (!isError) {
            exportFolderUri?.let {
                eventDispatcher.sendEvent(
                    AppEvent.JournalExported(it)
                )
            }
            viewState.routerExit()
        }
    }

    fun openExportFolder() {
        getSavedExportFolder()?.let { uri ->
            DocumentFile.fromTreeUri(context, uri)
                ?.findOrCreateDocumentFolder(context.getString(R.string.app_name))
                ?.findOrCreateDocumentFolder(EXPORT_DIR)
                ?.let { exportFolder ->
                    viewState.routerStartFlow(Screens.Common.actionOpenFolder(exportFolder.uri))
                }
        }
    }
}
