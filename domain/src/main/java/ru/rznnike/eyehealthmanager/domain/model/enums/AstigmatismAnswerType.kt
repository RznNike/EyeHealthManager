package ru.rznnike.eyehealthmanager.domain.model.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class AstigmatismAnswerType(
    val id: Int
) : Parcelable {
    OK(1),
    ANOMALY(2);

    companion object {
        operator fun get(id: Int?) = values().find { it.id == id } ?: OK

        fun parseName(string: String?) = values().find { it.toString() == string }
    }
}