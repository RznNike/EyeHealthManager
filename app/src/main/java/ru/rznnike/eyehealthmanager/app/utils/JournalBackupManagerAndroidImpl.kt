package ru.rznnike.eyehealthmanager.app.utils

import android.annotation.SuppressLint
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import ru.rznnike.eyehealthmanager.data.utils.DataConstants
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultFilter
import ru.rznnike.eyehealthmanager.domain.model.test.TestResult
import ru.rznnike.eyehealthmanager.domain.model.test.TestType
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.astigmatism.AstigmatismTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.colorperception.ColorPerceptionTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.contrast.ContrastTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.daltonism.DaltonismTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.nearfar.NearFarTestResult
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.toDate
import java.io.BufferedWriter
import java.io.IOException
import java.time.Clock
import java.util.EnumMap

class JournalBackupManagerAndroidImpl : JournalBackupManagerAndroid() {
    @SuppressLint("Recycle")
    override suspend fun exportJournal(
        filter: TestResultFilter,
        clock: Clock,
        readDataFromDBCallback: suspend (filter: TestResultFilter, pageOffset: Int) -> List<TestResult>
    ): Uri? {
        fun DocumentFile.findOrCreateDocumentFolder(name: String) = findFile(name) ?: createDirectory(name)

        var exportFolderUri: Uri? = null
        val exportFiles: MutableMap<TestType, DocumentFile> = EnumMap(TestType::class.java)
        val exportFileWriters: MutableMap<TestType, BufferedWriter> = EnumMap(TestType::class.java)
        val exportEntryCounters: MutableMap<TestType, Int> = EnumMap(TestType::class.java)

        context?.let { context ->
            context.contentResolver
                .persistedUriPermissions
                .firstOrNull()
                ?.uri
                ?.let { DocumentFile.fromTreeUri(context, it) }
                ?.findOrCreateDocumentFolder(DataConstants.APP_DIR)
                ?.findOrCreateDocumentFolder(DataConstants.EXPORT_DIR)
                ?.findOrCreateDocumentFolder(
                    clock.millis().toDate(GlobalConstants.DATE_PATTERN_FULL_FOR_PATH)
                )
                ?.let { folder ->
                    try {
                        exportFolderUri = folder.uri
                        TestType.entries.forEach { type ->
                            val fileName = type.toString().lowercase()
                            folder.createFile(
                                "text/tab-separated-values",
                                "${fileName}.tsv"
                            )?.let { file ->
                                context.contentResolver
                                    .openOutputStream(file.uri)
                                    ?.bufferedWriter()
                                    ?.let {
                                        exportFileWriters[type] = it
                                        it.appendLine(type.exportHeader)
                                    }

                                exportFiles[type] = file
                                exportEntryCounters[type] = 0
                            }
                        }

                        var dataCounter = 0
                        do {
                            val page = writeJournalPageToFiles(
                                filter = filter,
                                pageOffset = dataCounter,
                                exportFileWriters = exportFileWriters,
                                exportEntryCounters = exportEntryCounters,
                                readDataFromDBCallback = readDataFromDBCallback
                            )
                            dataCounter += page
                        } while (page == DataConstants.EXPORT_PAGE_SIZE)
                    } finally {
                        exportFileWriters.forEach {
                            try {
                                it.value.close()
                                if (exportEntryCounters[it.key] == 0) {
                                    exportFiles[it.key]?.delete()
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
        }
        return exportFolderUri
    }

    private suspend fun writeJournalPageToFiles(
        filter: TestResultFilter,
        pageOffset: Int,
        exportFileWriters: MutableMap<TestType, BufferedWriter>,
        exportEntryCounters: MutableMap<TestType, Int>,
        readDataFromDBCallback: suspend (filter: TestResultFilter, pageOffset: Int) -> List<TestResult>
    ): Int {
        val data = readDataFromDBCallback(filter, pageOffset)
        data.forEach { testResult ->
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
                exportFileWriters[type]?.appendLine(exportString)
                exportEntryCounters[type] = exportEntryCounters.getOrDefault(type, 0) + 1
            }
        }
        exportFileWriters.values.forEach {
            it.flush()
        }
        return data.size
    }

    @SuppressLint("Recycle")
    override suspend fun importJournal(
        importFolderUri: Uri,
        writeDataToDBCallback: suspend (List<TestResult>) -> Unit
    ) {
        context?.let { context ->
            DocumentFile.fromTreeUri(context, importFolderUri)
                ?.listFiles()
                ?.filter { it.isFile }
                ?.mapNotNull { file ->
                    val fileName = file.name?.removeSuffix(".tsv")
                    val type = TestType.entries.firstOrNull { fileName == it.name.lowercase() }
                    type?.let { type to file }
                }
                ?.distinctBy { it.first }
                ?.forEach { mappedFile ->
                    val type = mappedFile.first
                    val file = mappedFile.second
                    val resultsBuffer = mutableListOf<TestResult>()

                    context.contentResolver
                        .openInputStream(file.uri)
                        ?.bufferedReader()
                        ?.useLines { lines ->
                            lines
                                .mapNotNull { line ->
                                    when (type) {
                                        TestType.ACUITY -> AcuityTestResult.importFromString(line)
                                        TestType.ASTIGMATISM -> AstigmatismTestResult.importFromString(line)
                                        TestType.NEAR_FAR -> NearFarTestResult.importFromString(line)
                                        TestType.COLOR_PERCEPTION -> ColorPerceptionTestResult.importFromString(line)
                                        TestType.DALTONISM -> DaltonismTestResult.importFromString(line)
                                        TestType.CONTRAST -> ContrastTestResult.importFromString(line)
                                    }
                                }
                                .forEach {
                                    resultsBuffer.add(it)
                                    if (resultsBuffer.size >= DataConstants.IMPORT_PAGE_SIZE) {
                                        writeDataToDBCallback(resultsBuffer)
                                        resultsBuffer.clear()
                                    }
                                }
                            writeDataToDBCallback(resultsBuffer)
                        }
                }
        }
    }

    override fun getAvailableImportTypes(importFolderUri: Uri) =
        context?.let { context ->
            DocumentFile.fromTreeUri(context, importFolderUri)
                ?.listFiles()
                ?.filter { it.isFile }
                ?.mapNotNull { file ->
                    val fileName = file.name?.removeSuffix(".tsv")
                    TestType.entries.firstOrNull { fileName == it.name.lowercase() }
                }
                ?.distinct()
        } ?: emptyList()
}