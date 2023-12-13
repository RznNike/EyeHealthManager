package ru.rznnike.eyehealthmanager.data.gateway

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import ru.rznnike.eyehealthmanager.data.storage.repository.TestRepository
import ru.rznnike.eyehealthmanager.domain.gateway.TestGateway
import ru.rznnike.eyehealthmanager.domain.model.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.AstigmatismTestResult
import ru.rznnike.eyehealthmanager.domain.model.ColorPerceptionTestResult
import ru.rznnike.eyehealthmanager.domain.model.ContrastTestResult
import ru.rznnike.eyehealthmanager.domain.model.DaltonismTestResult
import ru.rznnike.eyehealthmanager.domain.model.NearFarTestResult
import ru.rznnike.eyehealthmanager.domain.model.TestResult
import ru.rznnike.eyehealthmanager.domain.model.TestResultFilterParams
import ru.rznnike.eyehealthmanager.domain.model.TestResultPagingParams
import ru.rznnike.eyehealthmanager.domain.model.enums.TestType
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.toDate
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.util.EnumMap

class TestGatewayImpl(
    private val testRepository: TestRepository,
    private val context: Context
) : TestGateway {
    private val exportFiles: MutableMap<TestType, DocumentFile> = EnumMap(TestType::class.java)
    private val exportFileWriters: MutableMap<TestType, BufferedWriter> = EnumMap(TestType::class.java)
    private val exportEntryCounters: MutableMap<TestType, Int> = EnumMap(TestType::class.java)

    override suspend fun getTestResults(params: TestResultPagingParams) =
        testRepository.getTests(params)

    override suspend fun addTestResult(item: TestResult) =
        testRepository.addTest(item)

    override suspend fun deleteTestResultById(id: Long) =
        testRepository.deleteTestById(id)

    override suspend fun deleteAllTestResults() =
        testRepository.deleteAllTests()

    override suspend fun deleteDuplicates() =
        testRepository.deleteDuplicates()

    @SuppressLint("Recycle")
    override suspend fun exportJournal(filterParams: TestResultFilterParams): Uri? {
        fun DocumentFile.findOrCreateDocumentFolder(name: String) = findFile(name) ?: createDirectory(name)

        var exportFolderUri: Uri? = null

        context.contentResolver
            .persistedUriPermissions
            .firstOrNull()
            ?.uri
            ?.let { DocumentFile.fromTreeUri(context, it) }
            ?.findOrCreateDocumentFolder(GlobalConstants.APP_DIR)
            ?.findOrCreateDocumentFolder(GlobalConstants.EXPORT_DIR)
            ?.findOrCreateDocumentFolder(
                System.currentTimeMillis().toDate(GlobalConstants.DATE_PATTERN_FULL_FOR_PATH)
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
                            filterParams = filterParams,
                            pageOffset = dataCounter
                        )
                        dataCounter += page
                    } while (page == GlobalConstants.EXPORT_PAGE_SIZE)
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
        return exportFolderUri
    }

    private suspend fun writeJournalPageToFiles(filterParams: TestResultFilterParams, pageOffset: Int): Int {
        val data = testRepository.getTests(
            TestResultPagingParams(
                limit = GlobalConstants.EXPORT_PAGE_SIZE,
                offset = pageOffset,
                filterParams = filterParams
            )
        )
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

    override suspend fun getAvailableImportTypes(importFolderUri: Uri) =
        DocumentFile.fromTreeUri(context, importFolderUri)
            ?.listFiles()
            ?.filter { it.isFile }
            ?.mapNotNull { file ->
                val fileName = file.name?.removeSuffix(".tsv")
                TestType.entries.firstOrNull { fileName == it.name.lowercase() }
            }
            ?.distinct()
            ?: emptyList()

















    private val files: MutableMap<TestType, DocumentFile> = EnumMap(TestType::class.java)
    private var importFolderUri: Uri? = null
    private var importFilesQueue: MutableMap<TestType, DocumentFile> = EnumMap(TestType::class.java)
    private var currentFileType: TestType = TestType.ACUITY
    private var currentFileReader: BufferedReader? = null

    override suspend fun importJournal(importFolderUri: Uri) {
        // отфильтровать файлы, соотнести с типами
        // цикл по файлам
            // цикл по страницам

        DocumentFile.fromTreeUri(context, importFolderUri)
            ?.listFiles()
            ?.filter { it.isFile }
            ?.mapNotNull { file ->
                val fileName = file.name?.removeSuffix(".tsv")
                TestType.entries.firstOrNull { fileName == it.name.lowercase() }
            }






        importFilesQueue.putAll(files)
        importFilePageToDatabase()
    }

    @SuppressLint("Recycle")
    private suspend fun importFilePageToDatabase() {
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
                    testRepository.addTests(testResults)
                    importFilePageToDatabase()
                }
            }
        }
    }
}
