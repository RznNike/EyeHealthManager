package ru.rznnike.eyehealthmanager.domain.interactor.test

import android.net.Uri
import ru.rznnike.eyehealthmanager.domain.gateway.TestGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseWithParams
import ru.rznnike.eyehealthmanager.domain.utils.JournalExportManager

class ImportJournalUseCase(
    private val testGateway: TestGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<ImportJournalUseCase.Parameters, Unit>(dispatcherProvider) {
    override suspend fun execute(parameters: Parameters) =
        testGateway.importJournal(
            importFolderUri = parameters.importFolderUri,
            manager = parameters.manager
        )

    data class Parameters(
        val importFolderUri: Uri,
        val manager: JournalExportManager
    )
}
