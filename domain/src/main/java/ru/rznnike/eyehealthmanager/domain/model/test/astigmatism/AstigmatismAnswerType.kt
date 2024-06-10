package ru.rznnike.eyehealthmanager.domain.model.test.astigmatism

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class AstigmatismAnswerType(
    val id: Int
) : Parcelable {
    OK(1),
    ANOMALY(2);

    companion object {
        operator fun get(id: Int?) = entries.find { it.id == id } ?: OK

        operator fun get(name: String?) = entries.find { it.toString() == name }
    }
}