package ru.rznnike.eyehealthmanager.domain.interactor.test

import android.net.Uri
import ru.rznnike.eyehealthmanager.domain.gateway.TestGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseWithParams
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultFilter

class ExportJournalUseCase(
    private val testGateway: TestGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<TestResultFilter, ExportJournalUseCase.Result>(dispatcherProvider) {
    override suspend fun execute(parameters: TestResultFilter) =
        Result(
            exportFolderUri = testGateway.exportJournal(parameters)
        )

    data class Result(
        val exportFolderUri: Uri?
    )
}
