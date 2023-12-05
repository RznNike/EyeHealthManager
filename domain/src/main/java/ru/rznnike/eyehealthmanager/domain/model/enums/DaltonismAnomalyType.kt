package ru.rznnike.eyehealthmanager.domain.model.enums

import androidx.annotation.StringRes
import ru.rznnike.eyehealthmanager.domain.R

enum class DaltonismAnomalyType(
    val id: Int,
    @StringRes val nameResId: Int
) {
    NONE(0, R.string.rabkin_result_normal),
    PROTANOPIA(1, R.string.rabkin_result_protanopia),
    DEITERANOPIA(2, R.string.rabkin_result_deiteranopia),
    PROTANOMALY_A(3, R.string.rabkin_result_protanomaly_a),
    PROTANOMALY_B(4, R.string.rabkin_result_protanomaly_b),
    PROTANOMALY_C(5, R.string.rabkin_result_protanomaly_c),
    DEITERANOMALY_A(6, R.string.rabkin_result_deiteranomaly_a),
    DEITERANOMALY_B(7, R.string.rabkin_result_deiteranomaly_b),
    DEITERANOMALY_C(8, R.string.rabkin_result_deiteranomaly_c),
    PATHOLOGY(9, R.string.rabkin_result_parhology);

    companion object {
        operator fun get(id: Int?) = values().find { it.id == id } ?: NONE

        fun parseName(string: String?) = values().find { it.toString() == string }
    }
}