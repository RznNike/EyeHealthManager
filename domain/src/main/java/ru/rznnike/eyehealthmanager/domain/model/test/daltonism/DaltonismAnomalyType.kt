package ru.rznnike.eyehealthmanager.domain.model.test.daltonism

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class DaltonismAnomalyType(
    val id: Int
) : Parcelable {
    NONE(0),
    PROTANOPIA(1),
    DEITERANOPIA(2),
    PROTANOMALY_A(3),
    PROTANOMALY_B(4),
    PROTANOMALY_C(5),
    DEITERANOMALY_A(6),
    DEITERANOMALY_B(7),
    DEITERANOMALY_C(8),
    PATHOLOGY(9);

    companion object {
        operator fun get(id: Int?) = entries.find { it.id == id } ?: NONE

        operator fun get(name: String?) = entries.find { it.toString() == name }
    }
}