package ru.rznnike.eyehealthmanager.app.presentation.journal.backup

import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView
import ru.rznnike.eyehealthmanager.domain.model.TestResultFilter

interface ExportJournalView : NavigationMvpView {
    @AddToEndSingle
    fun setProgress(show: Boolean, immediately: Boolean = true)

    @AddToEndSingle
    fun populateData(filter: TestResultFilter, folderPath: String?)

    @OneExecution
    fun selectExportFolder()
}
