package ru.rznnike.eyehealthmanager.app.presentation.main

import android.net.Uri
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView

interface MainView : NavigationMvpView {
    @AddToEndSingle
    fun setProgress(show: Boolean, immediately: Boolean = true)

    @OneExecution
    fun showSuccessfulExportDialog(uri: Uri)

    @OneExecution
    fun showSuccessfulImportDialog()

    @OneExecution
    fun showWelcomeDialog()

    @OneExecution
    fun showChangelogDialog()

    enum class NavigationTab {
        TESTS,
        JOURNAL,
        SETTINGS
    }
}
