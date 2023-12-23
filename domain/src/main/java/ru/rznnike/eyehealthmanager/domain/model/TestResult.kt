package ru.rznnike.eyehealthmanager.domain.model

import android.os.Parcelable

abstract class TestResult(
    open var id: Long = 0,
    open var timestamp: Long = 0
) : Parcelable {
    abstract fun contentEquals(other: TestResult?): Boolean

    abstract fun exportToString(): String
}