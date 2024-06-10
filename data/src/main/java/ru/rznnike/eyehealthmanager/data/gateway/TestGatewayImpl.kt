package ru.rznnike.eyehealthmanager.data.gateway

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import ru.rznnike.eyehealthmanager.data.utils.DataConstants
import ru.rznnike.eyehealthmanager.domain.storage.repository.TestRepository
import ru.rznnike.eyehealthmanager.domain.gateway.TestGateway
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.astigmatism.AstigmatismTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.colorperception.ColorPerceptionTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.contrast.ContrastTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.daltonism.DaltonismTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.nearfar.NearFarTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.TestResult
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultFilter
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultPagingParameters
import ru.rznnike.eyehealthmanager.domain.model.test.TestType
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.toDate
import java.io.BufferedWriter
import java.io.IOException
import java.time.Clock
import java.util.EnumMap

class TestGatewayImpl(
    private val testRepository: TestRepository,
    private val context: Context,
    private val clock: Clock
) : TestGateway {
    override suspend fun getTestResults(parameters: TestResultPagingParameters) =
        testRepository.getList(parameters)

    override suspend fun addTestResult(item: TestResult) =
        testRepository.add(item)

    override suspend fun deleteTestResultById(id: Long) =
        testRepository.delete(id)

    override suspend fun deleteAllTestResults() =
        testRepository.deleteAll()

    override suspend fun deleteDuplicates() =
        testRepository.deleteDuplicates()

    @SuppressLint("Recycle")
    override suspend fun exportJournal(filter: TestResultFilter): Uri? {
        fun DocumentFile.findOrCreateDocumentFolder(name: String) = findFile(name) ?: createDirectory(name)

        var exportFolderUri: Uri? = null
        val exportFiles: MutableMap<TestType, DocumentFile> = EnumMap(TestType::class.java)
        val exportFileWriters: MutableMap<TestType, BufferedWriter> = EnumMap(TestType::class.java)
        val exportEntryCounters: MutableMap<TestType, Int> = EnumMap(TestType::class.java)

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
                            exportEntryCounters = exportEntryCounters
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
        return exportFolderUri
    }

    private suspend fun writeJournalPageToFiles(
        filter: TestResultFilter,
        pageOffset: Int,
        exportFileWriters: MutableMap<TestType, BufferedWriter>,
        exportEntryCounters: MutableMap<TestType, Int>
    ): Int {
        val data = testRepository.getList(
            TestResultPagingParameters(
                limit = DataConstants.EXPORT_PAGE_SIZE,
                offset = pageOffset,
                filter = filter
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

    @SuppressLint("Recycle")
    override suspend fun importJournal(importFolderUri: Uri) {
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
                                    testRepository.add(resultsBuffer)
                                    resultsBuffer.clear()
                                }
                            }
                        testRepository.add(resultsBuffer)
                    }
            }
    }
}
