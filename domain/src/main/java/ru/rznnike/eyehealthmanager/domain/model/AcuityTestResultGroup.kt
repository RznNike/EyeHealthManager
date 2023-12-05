package ru.rznnike.eyehealthmanager.domain.model

data class AcuityTestResultGroup(
    var dateFrom: Long = 0,
    var dateTo: Long = 0,
    val values: MutableList<AcuityTestResult> = mutableListOf()
)