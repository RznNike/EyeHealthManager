package ru.rznnike.eyehealthmanager.domain.model.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class NearFarAnswerType(
    val id: Int
) : Parcelable {
    RED_BETTER(1),
    GREEN_BETTER(2),
    EQUAL(0);

    companion object {
        operator fun get(id: Int?) = values().find { it.id == id } ?: EQUAL

        fun parseName(string: String?) = values().find { it.toString() == string }
    }
}