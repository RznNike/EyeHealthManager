package ru.rznnike.eyehealthmanager.domain.model

abstract class TestResult(
    open var id: Long = 0,
    open var timestamp: Long = 0
) {
    abstract fun contentEquals(other: TestResult?): Boolean

    abstract fun exportToString(): String
}