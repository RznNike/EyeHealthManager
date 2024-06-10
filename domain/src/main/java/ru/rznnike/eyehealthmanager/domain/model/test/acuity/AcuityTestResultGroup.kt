package ru.rznnike.eyehealthmanager.domain.model.test.acuity

import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestResult

data class AcuityTestResultGroup(
    var dateFrom: Long = 0,
    var dateTo: Long = 0,
    val values: MutableList<AcuityTestResult> = mutableListOf()
)