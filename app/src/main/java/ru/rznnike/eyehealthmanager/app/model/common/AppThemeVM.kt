package ru.rznnike.eyehealthmanager.app.model.common

import androidx.annotation.StringRes
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.domain.model.common.AppTheme

enum class AppThemeVM(
    val data: AppTheme,
    @StringRes val nameResId: Int
) {
    LIGHT(
        data = AppTheme.LIGHT,
        nameResId = R.string.theme_light
    ),
    DARK(
        data = AppTheme.DARK,
        nameResId = R.string.theme_dark
    ),
    SYSTEM(
        data = AppTheme.SYSTEM,
        nameResId = R.string.theme_system
    );

    companion object {
        operator fun get(data: AppTheme?) = entries.find { it.data == data } ?: SYSTEM
    }
}