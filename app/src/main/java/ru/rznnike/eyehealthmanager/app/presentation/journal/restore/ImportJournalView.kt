package ru.rznnike.eyehealthmanager.app.presentation.journal.restore

import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView
import ru.rznnike.eyehealthmanager.domain.model.test.TestType

interface ImportJournalView : NavigationMvpView {
    @AddToEndSingle
    fun setProgress(show: Boolean, immediately: Boolean = true)

    @AddToEndSingle
    fun populateData(folderPath: String?, availableImportTypes: List<TestType>)

    @OneExecution
    fun selectImportFolder()
}
