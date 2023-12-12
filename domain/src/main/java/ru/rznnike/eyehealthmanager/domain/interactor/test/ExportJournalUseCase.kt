package ru.rznnike.eyehealthmanager.domain.interactor.test

import android.net.Uri
import ru.rznnike.eyehealthmanager.domain.gateway.TestGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseWithParams
import ru.rznnike.eyehealthmanager.domain.model.TestResultFilterParams

class ExportJournalUseCase(
    private val testGateway: TestGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<TestResultFilterParams, ExportJournalUseCase.Result>(dispatcherProvider.io) {
    override suspend fun execute(parameters: TestResultFilterParams) =
        Result(
            exportFolderUri = testGateway.exportJournal(parameters)
        )

    data class Result(
        val exportFolderUri: Uri?
    )
}
