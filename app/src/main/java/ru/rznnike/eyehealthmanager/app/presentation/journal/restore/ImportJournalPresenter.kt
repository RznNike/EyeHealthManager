package ru.rznnike.eyehealthmanager.app.presentation.journal.restore

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
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
import ru.rznnike.eyehealthmanager.domain.interactor.test.GetAvailableImportTypesUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.test.ImportJournalUseCase
import ru.rznnike.eyehealthmanager.domain.model.*
import ru.rznnike.eyehealthmanager.domain.model.enums.TestType
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import java.io.BufferedReader
import java.util.*

@InjectViewState
class ImportJournalPresenter : BasePresenter<ImportJournalView>() {
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val context: Context by inject()
    private val getAvailableImportTypesUseCase: GetAvailableImportTypesUseCase by inject()
    private val importJournalUseCase: ImportJournalUseCase by inject()

    private val files: MutableMap<TestType, DocumentFile> = EnumMap(TestType::class.java)
    private var importFolderUri: Uri? = null
    private var importFilesQueue: MutableMap<TestType, DocumentFile> = EnumMap(TestType::class.java)
    private var currentFileType: TestType = TestType.ACUITY
    private var currentFileReader: BufferedReader? = null
    private var startImportAutomatically = false

    override fun onFirstViewAttach() {
        populateData()
    }

    private fun populateData() = viewState.populateData(
        availableBackups = files.keys.toList(),
        folderPath = importFolderUri?.lastPathSegment
    )

    fun onFolderSelected(uri: Uri) {
        importFolderUri = uri

        files.clear()
        importFolderUri?.let { importFolderUri ->
            DocumentFile.fromTreeUri(context, importFolderUri)
                ?.listFiles()
                ?.filter { it.isFile }
                ?.forEach { file ->
                    val fileName = file.name?.removeSuffix(".tsv")
                    TestType.entries
                        .firstOrNull { type ->
                            fileName == type.name.lowercase()
                        }
                        ?.let { type ->
                            files[type] = file
                        }
                }
        }
        populateData()

        if (startImportAutomatically) {
            startImportAutomatically = false
            startImport()
        }
    }

    fun startImport() {
        when {
            importFolderUri == null -> {
                startImportAutomatically = true
                viewState.selectImportFolder()
            }
            files.isEmpty() -> {
                notifier.sendMessage(R.string.error_no_backup_in_folder)
            }
            else -> {
                presenterScope.launch {
                    viewState.setProgress(true)
                    importFilesQueue.putAll(files)
                    importFilePageToDatabase()
                }
            }
        }
    }

    @SuppressLint("Recycle")
    private fun importFilePageToDatabase() {
        if (currentFileReader == null) {
            importFilesQueue.entries.firstOrNull()?.let { entry ->
                val type = entry.key
                val file = entry.value
                context.contentResolver.openInputStream(file.uri)?.let { inputStream ->
                    currentFileReader = inputStream.bufferedReader()
                    currentFileType = type
                    importFilesQueue.remove(type)
                }
            }
            if (currentFileReader == null) {
                finishImport()
            }
        }
        currentFileReader?.let { fileReader ->
            val lines = mutableListOf<String>()
            while (lines.size < GlobalConstants.IMPORT_PAGE_SIZE) {
                val line = fileReader.readLine()
                if (line == null) {
                    fileReader.close()
                    currentFileReader = null
                    break
                } else {
                    lines.add(line)
                }
            }
            if (lines.isEmpty()) {
                importFilePageToDatabase()
            } else {
                val testResults = lines.mapNotNull {
                    when (currentFileType) {
                        TestType.ACUITY -> AcuityTestResult.importFromString(it)
                        TestType.ASTIGMATISM -> AstigmatismTestResult.importFromString(it)
                        TestType.NEAR_FAR -> NearFarTestResult.importFromString(it)
                        TestType.COLOR_PERCEPTION -> ColorPerceptionTestResult.importFromString(it)
                        TestType.DALTONISM -> DaltonismTestResult.importFromString(it)
                        TestType.CONTRAST -> ContrastTestResult.importFromString(it)
                    }
                }
                if (testResults.isEmpty()) {
                    importFilePageToDatabase()
                } else {
                    presenterScope.launch {
                        addTestResultsUseCase(testResults).process(
                            {
                                importFilePageToDatabase()
                            }, { error ->
                                withContext(Dispatchers.IO) {
                                    fileReader.close()
                                    currentFileReader = null
                                }

                                viewState.setProgress(false)
                                errorHandler.proceed(error) {
                                    notifier.sendMessage(it)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun finishImport() {
        viewState.setProgress(false)
        importFolderUri?.let {
            eventDispatcher.sendEvent(
                AppEvent.JournalImported(it)
            )
        }
        viewState.routerExit()
    }

    fun openImportFolder() {
        importFolderUri?.let {
            viewState.routerStartFlow(Screens.Common.actionOpenFolder(it))
        }
    }
}
