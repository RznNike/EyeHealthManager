package ru.rznnike.eyehealthmanager.app.presentation.main.journal

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.rznnike.eyehealthmanager.app.global.presentation.PagerView
import ru.rznnike.eyehealthmanager.domain.model.test.TestResult
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultFilter

interface JournalView : PagerView {
    @AddToEndSingle
    fun populateData(data: List<TestResult>, filter: TestResultFilter)
}
