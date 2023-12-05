package ru.rznnike.eyehealthmanager.app.presentation.journal.restore

import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView
import ru.rznnike.eyehealthmanager.domain.model.enums.TestType

interface ImportJournalView : NavigationMvpView {
    @AddToEndSingle
    fun setProgress(show: Boolean, immediately: Boolean = true)

    @AddToEndSingle
    fun populateData(availableBackups: List<TestType>, folderPath: String?)

    @OneExecution
    fun selectImportFolder()
}
