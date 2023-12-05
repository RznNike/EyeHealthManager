package ru.rznnike.eyehealthmanager.domain.model

data class DynamicCorrectionsData(
    var beginning: Double = 0.0,
    var middle: Double = 0.0,
    var end: Double = 0.0,
    val doctorCorrectionsCalculated: Boolean = false
)