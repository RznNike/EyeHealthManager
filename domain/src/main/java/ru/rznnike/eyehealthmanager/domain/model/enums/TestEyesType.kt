package ru.rznnike.eyehealthmanager.domain.model.enums

import androidx.annotation.StringRes
import ru.rznnike.eyehealthmanager.domain.R

enum class TestEyesType(
    val id: Int,
    @StringRes val nameResId: Int
) {
    BOTH(1, R.string.eyes_type_both),
    LEFT(2, R.string.eyes_type_left),
    RIGHT(3, R.string.eyes_type_right);

    companion object {
        operator fun get(id: Int?) = entries.find { it.id == id } ?: BOTH

        operator fun get(name: String?) = entries.find { it.toString() == name }
    }
}