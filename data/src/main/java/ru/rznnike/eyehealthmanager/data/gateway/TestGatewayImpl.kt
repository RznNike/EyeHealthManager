package ru.rznnike.eyehealthmanager.data.gateway

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import ru.rznnike.eyehealthmanager.data.R
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

    override suspend fun addTestResults(items: List<TestResult>) =
        testRepository.addTests(items)

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
            ?.let { uri ->
                DocumentFile.fromTreeUri(context, uri)
                    ?.findOrCreateDocumentFolder(context.getString(R.string.app_name))
                    ?.findOrCreateDocumentFolder(GlobalConstants.EXPORT_DIR)
                    ?.findOrCreateDocumentFolder(
                        System.currentTimeMillis().toDate(GlobalConstants.DATE_PATTERN_FULL_FOR_PATH)
                    )
                    ?.let { folder ->
                        exportFolderUri = folder.uri
                        TestType.entries.forEach { type ->
                            val fileName = type.toString().lowercase()
                            folder.createFile(
                                "text/tab-separated-values",
                                "${fileName}.tsv"
                            )?.let { file ->
                                try {
                                    context.contentResolver.openOutputStream(file.uri)?.let { outputStream ->
                                        outputStream.bufferedWriter().let {
                                            exportFileWriters[type] = it
                                            it.appendLine(type.exportHeader)
                                        }
                                    }
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }

                                exportFiles[type] = file
                                exportEntryCounters[type] = 0
                            }
                        }

                        try {
                            exportDatabasePageToFiles(
                                filterParams = filterParams,
                                pageOffset = 0
                            )
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

    private suspend fun exportDatabasePageToFiles(filterParams: TestResultFilterParams, pageOffset: Int) {
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
                exportEntryCounters[type] = (exportEntryCounters[type] ?: 0) + 1
            }
        }
        exportFileWriters.values.forEach {
            it.flush()
        }
        if (data.size >= GlobalConstants.EXPORT_PAGE_SIZE) {
            exportDatabasePageToFiles(
                filterParams = filterParams,
                pageOffset = pageOffset + GlobalConstants.EXPORT_PAGE_SIZE
            )
        }
    }
}
