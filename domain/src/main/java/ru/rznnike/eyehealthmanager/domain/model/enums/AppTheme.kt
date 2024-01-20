package ru.rznnike.eyehealthmanager.domain.model.enums

import androidx.annotation.StringRes
import ru.rznnike.eyehealthmanager.domain.R

enum class AppTheme(
    val id: Int,
    @StringRes val nameResId: Int
) {
    LIGHT(
        id = 1,
        nameResId = R.string.theme_light
    ),
    DARK(
        id = 2,
        nameResId = R.string.theme_dark
    ),
    SYSTEM(
        id = 3,
        nameResId = R.string.theme_system
    );

    companion object {
        operator fun get(id: Int?) = entries.find { it.id == id } ?: SYSTEM
    }
}