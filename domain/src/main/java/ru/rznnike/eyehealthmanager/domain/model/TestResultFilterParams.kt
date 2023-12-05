package ru.rznnike.eyehealthmanager.domain.model

import ru.rznnike.eyehealthmanager.domain.model.enums.TestType

data class TestResultFilterParams(
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