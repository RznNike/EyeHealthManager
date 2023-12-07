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
        operator fun get(id: Int?) = entries.find { it.id == id } ?: EQUAL

        operator fun get(name: String?) = entries.find { it.toString() == name }
    }
}