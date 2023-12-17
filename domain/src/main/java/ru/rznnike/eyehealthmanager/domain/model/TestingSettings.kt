package ru.rznnike.eyehealthmanager.domain.model

data class TestingSettings(
    var armsLength: Int = 0,
    var dpmm: Float = 0f,
    var replaceBeginningWithMorning: Boolean = false,
    var enableAutoDayPart: Boolean = false,
    var timeToDayBeginning: Long = 0,
    var timeToDayMiddle: Long = 0,
    var timeToDayEnd: Long = 0
)