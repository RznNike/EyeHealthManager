package ru.rznnike.eyehealthmanager.domain.interactor.test

import android.net.Uri
import ru.rznnike.eyehealthmanager.domain.gateway.TestGateway
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseWithParams

class ImportJournalUseCase(
    private val testGateway: TestGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<Uri, Unit>(dispatcherProvider.io) {
    override suspend fun execute(parameters: Uri) =
        testGateway.importJournal(parameters)
}
