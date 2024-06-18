package ru.rznnike.eyehealthmanager.domain.model.journal

import ru.rznnike.eyehealthmanager.domain.model.test.TestType

data class TestResultFilter(
    var filterByDate: Boolean = false,
    var filterByType: Boolean = false,
    var dateFrom: Long = 0,
    var dateTo: Long = 0,
    val selectedTestTypes: MutableList<TestType> = mutableListOf()
) {
    fun deepCopy() = copy(
        selectedTestTypes = selectedTestTypes.toMutableList()
    )
}