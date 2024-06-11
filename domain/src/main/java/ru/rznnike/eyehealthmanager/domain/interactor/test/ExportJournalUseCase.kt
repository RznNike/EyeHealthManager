package ru.rznnike.eyehealthmanager.domain.interactor.test

import android.net.Uri
import ru.rznnike.eyehealthmanager.domain.gateway.TestGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseWithParams
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultFilter
import ru.rznnike.eyehealthmanager.domain.utils.JournalExportManager

class ExportJournalUseCase(
    private val testGateway: TestGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<ExportJournalUseCase.Parameters, ExportJournalUseCase.Result>(dispatcherProvider) {
    override suspend fun execute(parameters: Parameters) =
        Result(
            exportFolderUri = testGateway.exportJournal(
                filter = parameters.filter,
                manager = parameters.manager
            )
        )

    data class Parameters(
        val filter: TestResultFilter,
        val manager: JournalExportManager
    )

    data class Result(
        val exportFolderUri: Uri?
    )
}
